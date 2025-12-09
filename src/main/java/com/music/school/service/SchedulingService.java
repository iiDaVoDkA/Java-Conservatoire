package com.music.school.service;

import com.music.school.enums.ActivityStatus;
import com.music.school.interfaces.Schedulable;
import com.music.school.model.person.Student;
import com.music.school.model.person.Teacher;
import com.music.school.model.resource.Room;
import com.music.school.model.scheduling.Lesson;
import com.music.school.model.scheduling.RoomBooking;
import com.music.school.model.scheduling.ScheduledActivity;
import com.music.school.model.service.CoursePackage;
import com.music.school.repository.DataRepository;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing scheduling operations.
 * Handles conflict detection, validation, and resource booking.
 * 
 * Demonstrates:
 * - Use of Schedulable interface for polymorphic conflict detection
 * - Business rule enforcement
 */
public class SchedulingService {
    
    private final DataRepository repository;
    
    public SchedulingService() {
        this.repository = DataRepository.getInstance();
    }
    
    /**
     * Schedule a new lesson with conflict detection.
     * 
     * @param teacherId the teacher's ID
     * @param studentIds list of student IDs
     * @param roomId the room ID
     * @param instrument the instrument
     * @param dateTime the scheduled date and time
     * @param durationMinutes the duration in minutes
     * @return the scheduled lesson or null if conflicts exist
     */
    public Lesson scheduleLesson(String teacherId, List<String> studentIds, String roomId,
                                  String instrument, LocalDateTime dateTime, int durationMinutes) {
        
        // Validate teacher
        Teacher teacher = repository.getTeacher(teacherId);
        if (teacher == null || !teacher.isActive()) {
            throw new IllegalArgumentException("Teacher not found or inactive: " + teacherId);
        }
        
        // Validate teacher can teach this instrument
        if (!teacher.canTeach(instrument)) {
            throw new IllegalArgumentException("Teacher cannot teach " + instrument);
        }
        
        // Validate students
        for (String studentId : studentIds) {
            Student student = repository.getStudent(studentId);
            if (student == null || !student.isActive()) {
                throw new IllegalArgumentException("Student not found or inactive: " + studentId);
            }
        }
        
        // Validate room
        Room room = repository.getRoom(roomId);
        if (room == null || !room.isAvailable()) {
            throw new IllegalArgumentException("Room not found or unavailable: " + roomId);
        }
        
        // Check for conflicts
        List<String> conflicts = checkSchedulingConflicts(teacherId, studentIds, roomId, 
                                                           dateTime, durationMinutes);
        if (!conflicts.isEmpty()) {
            throw new IllegalStateException("Scheduling conflicts detected:\n" + 
                String.join("\n", conflicts));
        }
        
        // Check teacher availability
        if (!teacher.isAvailableAt(dateTime, durationMinutes)) {
            throw new IllegalStateException("Teacher is not available at the requested time");
        }
        
        // Check room availability
        if (!room.isAvailableAt(dateTime, durationMinutes)) {
            throw new IllegalStateException("Room is not available at the requested time");
        }
        
        // Create the lesson
        Lesson lesson = new Lesson(dateTime, Duration.ofMinutes(durationMinutes), roomId,
                                   teacherId, studentIds, instrument);
        
        // Book resources
        var teacherSlot = new com.music.school.interfaces.Bookable.TimeSlot(
            dateTime, dateTime.plusMinutes(durationMinutes), lesson.getId());
        teacher.addBooking(teacherSlot);
        
        var roomSlot = new com.music.school.interfaces.Bookable.TimeSlot(
            dateTime, dateTime.plusMinutes(durationMinutes), lesson.getId());
        room.addBooking(roomSlot);
        
        // Save the lesson
        repository.addScheduledActivity(lesson);
        
        return lesson;
    }
    
    /**
     * Schedule an individual lesson.
     */
    public Lesson scheduleIndividualLesson(String teacherId, String studentId, String roomId,
                                            String instrument, LocalDateTime dateTime, 
                                            int durationMinutes) {
        List<String> studentIds = new ArrayList<>();
        studentIds.add(studentId);
        return scheduleLesson(teacherId, studentIds, roomId, instrument, dateTime, durationMinutes);
    }
    
    /**
     * Book a room for practice.
     */
    public RoomBooking bookRoom(String studentId, String roomId, LocalDateTime dateTime,
                                 int durationMinutes, BigDecimal hourlyRate, String purpose) {
        
        // Validate student
        Student student = repository.getStudent(studentId);
        if (student == null || !student.isActive()) {
            throw new IllegalArgumentException("Student not found or inactive: " + studentId);
        }
        
        // Validate room
        Room room = repository.getRoom(roomId);
        if (room == null || !room.isAvailable()) {
            throw new IllegalArgumentException("Room not found or unavailable: " + roomId);
        }
        
        // Check room availability
        if (!room.isAvailableAt(dateTime, durationMinutes)) {
            throw new IllegalStateException("Room is not available at the requested time");
        }
        
        // Create booking
        RoomBooking booking = new RoomBooking(dateTime, Duration.ofMinutes(durationMinutes),
                                              roomId, studentId, hourlyRate, purpose);
        
        // Book the room
        var slot = new com.music.school.interfaces.Bookable.TimeSlot(
            dateTime, dateTime.plusMinutes(durationMinutes), booking.getId());
        room.addBooking(slot);
        
        // Save the booking
        repository.addScheduledActivity(booking);
        
        return booking;
    }
    
    /**
     * Check for scheduling conflicts.
     * Uses polymorphism through Schedulable interface.
     * 
     * @return list of conflict descriptions
     */
    public List<String> checkSchedulingConflicts(String teacherId, List<String> studentIds,
                                                  String roomId, LocalDateTime dateTime,
                                                  int durationMinutes) {
        List<String> conflicts = new ArrayList<>();
        LocalDateTime endTime = dateTime.plusMinutes(durationMinutes);
        
        // Create a temporary schedulable for comparison
        Schedulable tempSchedulable = new Schedulable() {
            @Override
            public LocalDateTime getScheduledDateTime() { return dateTime; }
            @Override
            public void setScheduledDateTime(LocalDateTime dt) {}
            @Override
            public Duration getDuration() { return Duration.ofMinutes(durationMinutes); }
            @Override
            public void setDuration(Duration d) {}
            @Override
            public ActivityStatus getStatus() { return ActivityStatus.SCHEDULED; }
            @Override
            public void setStatus(ActivityStatus s) {}
        };
        
        // Check teacher conflicts
        List<Lesson> teacherLessons = repository.getTeacherLessons(teacherId);
        for (Lesson lesson : teacherLessons) {
            if (lesson.getStatus() == ActivityStatus.SCHEDULED && 
                tempSchedulable.conflictsWith(lesson)) {
                conflicts.add("Teacher has another lesson: " + lesson.getId() + 
                            " at " + lesson.getScheduledDateTime());
            }
        }
        
        // Check student conflicts
        for (String studentId : studentIds) {
            List<Lesson> studentLessons = repository.getStudentLessons(studentId);
            for (Lesson lesson : studentLessons) {
                if (lesson.getStatus() == ActivityStatus.SCHEDULED && 
                    tempSchedulable.conflictsWith(lesson)) {
                    conflicts.add("Student " + studentId + " has another lesson: " + 
                                lesson.getId() + " at " + lesson.getScheduledDateTime());
                }
            }
        }
        
        // Check room conflicts
        List<ScheduledActivity> roomActivities = repository.getRoomActivities(roomId);
        for (ScheduledActivity activity : roomActivities) {
            if (activity.getStatus() == ActivityStatus.SCHEDULED && 
                tempSchedulable.conflictsWith(activity)) {
                conflicts.add("Room has another booking: " + activity.getId() + 
                            " at " + activity.getScheduledDateTime());
            }
        }
        
        return conflicts;
    }
    
    /**
     * Cancel an activity.
     * Business rule: cancellation within 24 hours counts as consumed.
     * 
     * @return true if cancelled without penalty, false if counts as consumed
     */
    public boolean cancelActivity(String activityId) {
        ScheduledActivity activity = repository.getScheduledActivity(activityId);
        if (activity == null) {
            throw new IllegalArgumentException("Activity not found: " + activityId);
        }
        
        boolean withoutPenalty = activity.cancel();
        
        // Release booked resources
        releaseResources(activity);
        
        return withoutPenalty;
    }
    
    /**
     * Complete an activity and consume hours if applicable.
     */
    public void completeActivity(String activityId) {
        ScheduledActivity activity = repository.getScheduledActivity(activityId);
        if (activity == null) {
            throw new IllegalArgumentException("Activity not found: " + activityId);
        }
        
        activity.complete();
        
        // If it's a lesson, consume hours from student packages
        if (activity instanceof Lesson lesson) {
            consumeLessonHours(lesson);
        }
    }
    
    /**
     * Consume hours from student packages for a lesson.
     */
    private void consumeLessonHours(Lesson lesson) {
        int hoursToConsume = lesson.getHoursPerStudent();
        
        for (String studentId : lesson.getStudentIds()) {
            Student student = repository.getStudent(studentId);
            if (student != null) {
                student.consumeHours(hoursToConsume);
            }
        }
    }
    
    /**
     * Release resources when an activity is cancelled.
     */
    private void releaseResources(ScheduledActivity activity) {
        // Release room
        if (activity.getRoomId() != null) {
            Room room = repository.getRoom(activity.getRoomId());
            if (room != null) {
                var slot = new com.music.school.interfaces.Bookable.TimeSlot(
                    activity.getScheduledDateTime(),
                    activity.getEndDateTime(),
                    activity.getId());
                room.removeBooking(slot);
            }
        }
        
        // If lesson, release teacher
        if (activity instanceof Lesson lesson) {
            Teacher teacher = repository.getTeacher(lesson.getTeacherId());
            if (teacher != null) {
                var slot = new com.music.school.interfaces.Bookable.TimeSlot(
                    activity.getScheduledDateTime(),
                    activity.getEndDateTime(),
                    activity.getId());
                teacher.removeBooking(slot);
            }
        }
    }
    
    /**
     * Reschedule an activity.
     */
    public boolean rescheduleActivity(String activityId, LocalDateTime newDateTime) {
        ScheduledActivity activity = repository.getScheduledActivity(activityId);
        if (activity == null) {
            throw new IllegalArgumentException("Activity not found: " + activityId);
        }
        
        if (!activity.canReschedule()) {
            return false;
        }
        
        // Check for conflicts at new time
        if (activity instanceof Lesson lesson) {
            List<String> conflicts = checkSchedulingConflicts(
                lesson.getTeacherId(),
                lesson.getStudentIds(),
                lesson.getRoomId(),
                newDateTime,
                (int) lesson.getDurationMinutes());
            
            if (!conflicts.isEmpty()) {
                throw new IllegalStateException("Cannot reschedule - conflicts at new time:\n" + 
                    String.join("\n", conflicts));
            }
        }
        
        // Release old resources and book new
        releaseResources(activity);
        
        activity.setScheduledDateTime(newDateTime);
        
        // Re-book resources at new time
        if (activity instanceof Lesson lesson) {
            Teacher teacher = repository.getTeacher(lesson.getTeacherId());
            if (teacher != null) {
                var teacherSlot = new com.music.school.interfaces.Bookable.TimeSlot(
                    newDateTime, newDateTime.plus(activity.getDuration()), activity.getId());
                teacher.addBooking(teacherSlot);
            }
        }
        
        if (activity.getRoomId() != null) {
            Room room = repository.getRoom(activity.getRoomId());
            if (room != null) {
                var roomSlot = new com.music.school.interfaces.Bookable.TimeSlot(
                    newDateTime, newDateTime.plus(activity.getDuration()), activity.getId());
                room.addBooking(roomSlot);
            }
        }
        
        return true;
    }
    
    /**
     * Get upcoming activities sorted by date.
     */
    public List<ScheduledActivity> getUpcomingActivities() {
        LocalDateTime now = LocalDateTime.now();
        return repository.getAllScheduledActivities().stream()
            .filter(a -> a.getScheduledDateTime().isAfter(now))
            .filter(a -> a.getStatus() == ActivityStatus.SCHEDULED)
            .sorted(Comparator.comparing(ScheduledActivity::getScheduledDateTime))
            .collect(Collectors.toList());
    }
    
    /**
     * Get activities for today.
     */
    public List<ScheduledActivity> getTodayActivities() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        
        return repository.getAllScheduledActivities().stream()
            .filter(a -> !a.getScheduledDateTime().isBefore(startOfDay))
            .filter(a -> a.getScheduledDateTime().isBefore(endOfDay))
            .sorted(Comparator.comparing(ScheduledActivity::getScheduledDateTime))
            .collect(Collectors.toList());
    }
    
    /**
     * Link a lesson to a course package.
     */
    public void linkLessonToPackage(String lessonId, String packageId) {
        ScheduledActivity activity = repository.getScheduledActivity(lessonId);
        if (!(activity instanceof Lesson lesson)) {
            throw new IllegalArgumentException("Activity is not a lesson: " + lessonId);
        }
        
        CoursePackage pkg = (CoursePackage) repository.getService(packageId);
        if (pkg == null) {
            throw new IllegalArgumentException("Package not found: " + packageId);
        }
        
        lesson.setPackageId(packageId);
    }
}

