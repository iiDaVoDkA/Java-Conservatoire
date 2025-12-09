package com.music.school.data;

import com.music.school.enums.ExamResult;
import com.music.school.enums.Level;
import com.music.school.model.exam.Exam;
import com.music.school.model.person.Student;
import com.music.school.model.person.Teacher;
import com.music.school.model.resource.Instrument;
import com.music.school.model.resource.Room;
import com.music.school.model.scheduling.Lesson;
import com.music.school.model.service.CoursePackage;
import com.music.school.model.service.IndividualLesson;
import com.music.school.model.service.InstrumentRental;
import com.music.school.repository.DataRepository;
import com.music.school.service.ExamService;
import com.music.school.service.PaymentService;
import com.music.school.service.SchedulingService;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

/**
 * Initializes the system with test data for demonstration purposes.
 * Creates students, teachers, rooms, instruments, packages, lessons, and exams.
 */
public class TestDataInitializer {
    
    private final DataRepository repository;
    private final SchedulingService schedulingService;
    private final PaymentService paymentService;
    private final ExamService examService;
    
    public TestDataInitializer() {
        this.repository = DataRepository.getInstance();
        this.schedulingService = new SchedulingService();
        this.paymentService = new PaymentService();
        this.examService = new ExamService();
    }
    
    /**
     * Initialize all test data.
     */
    public void initializeAll() {
        System.out.println("Initializing test data...\n");
        
        initializeRooms();
        System.out.println("✓ Rooms created");
        
        initializeInstruments();
        System.out.println("✓ Instruments created");
        
        initializeTeachers();
        System.out.println("✓ Teachers created");
        
        initializeStudents();
        System.out.println("✓ Students created");
        
        initializePackages();
        System.out.println("✓ Course packages created");
        
        initializeLessons();
        System.out.println("✓ Lessons scheduled");
        
        initializeExams();
        System.out.println("✓ Exams created");
        
        initializePayments();
        System.out.println("✓ Payments recorded");
        
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("     TEST DATA INITIALIZATION COMPLETE  ");
        System.out.println("═══════════════════════════════════════");
        System.out.println(String.format("Students:    %d", repository.getStudentCount()));
        System.out.println(String.format("Teachers:    %d", repository.getTeacherCount()));
        System.out.println(String.format("Rooms:       %d", repository.getAllRooms().size()));
        System.out.println(String.format("Instruments: %d", repository.getAllInstruments().size()));
        System.out.println(String.format("Services:    %d", repository.getAllServices().size()));
        System.out.println(String.format("Activities:  %d", repository.getScheduledActivityCount()));
        System.out.println(String.format("Exams:       %d", repository.getAllExams().size()));
        System.out.println("═══════════════════════════════════════\n");
    }
    
    /**
     * Initialize rooms.
     */
    private void initializeRooms() {
        Room room1 = new Room("Piano Studio A", 2, Arrays.asList("Piano"));
        room1.addEquipment("Grand Piano");
        room1.addEquipment("Music Stand");
        room1.addEquipment("Metronome");
        repository.addRoom(room1);
        
        Room room2 = new Room("Piano Studio B", 2, Arrays.asList("Piano"));
        room2.addEquipment("Upright Piano");
        room2.addEquipment("Music Stand");
        repository.addRoom(room2);
        
        Room room3 = new Room("String Room", 4, Arrays.asList("Violin", "Viola", "Cello"));
        room3.addEquipment("Music Stands");
        room3.addEquipment("Tuner");
        repository.addRoom(room3);
        
        Room room4 = new Room("Guitar Studio", 3, Arrays.asList("Guitar", "Bass Guitar"));
        room4.addEquipment("Amplifiers");
        room4.addEquipment("Music Stands");
        repository.addRoom(room4);
        
        Room room5 = new Room("Percussion Room", 4, Arrays.asList("Drums", "Percussion"));
        room5.addEquipment("Drum Kit");
        room5.addEquipment("Practice Pads");
        room5.addEquipment("Cymbals");
        repository.addRoom(room5);
        
        Room room6 = new Room("Ensemble Hall", 15, Arrays.asList("Piano", "Violin", "Guitar"));
        room6.addEquipment("Grand Piano");
        room6.addEquipment("Audio System");
        room6.addEquipment("Multiple Music Stands");
        repository.addRoom(room6);
        
        Room room7 = new Room("Practice Room 1", 1, List.of());
        room7.addEquipment("Music Stand");
        repository.addRoom(room7);
        
        Room room8 = new Room("Practice Room 2", 1, List.of());
        room8.addEquipment("Music Stand");
        repository.addRoom(room8);
    }
    
    /**
     * Initialize instruments for rental.
     */
    private void initializeInstruments() {
        // Violins
        Instrument violin1 = new Instrument("Student Violin 4/4", "Violin", "Yamaha", 
            new BigDecimal("5.00"), new BigDecimal("100.00"));
        violin1.setCondition("Good");
        repository.addInstrument(violin1);
        
        Instrument violin2 = new Instrument("Student Violin 3/4", "Violin", "Stentor",
            new BigDecimal("4.00"), new BigDecimal("80.00"));
        violin2.setCondition("Excellent");
        repository.addInstrument(violin2);
        
        // Guitars
        Instrument guitar1 = new Instrument("Classical Guitar", "Guitar", "Yamaha",
            new BigDecimal("4.00"), new BigDecimal("75.00"));
        guitar1.setCondition("Good");
        repository.addInstrument(guitar1);
        
        Instrument guitar2 = new Instrument("Electric Guitar", "Guitar", "Fender",
            new BigDecimal("6.00"), new BigDecimal("150.00"));
        guitar2.setCondition("Excellent");
        repository.addInstrument(guitar2);
        
        // Cellos
        Instrument cello1 = new Instrument("Student Cello 4/4", "Cello", "Cecilio",
            new BigDecimal("8.00"), new BigDecimal("200.00"));
        cello1.setCondition("Good");
        repository.addInstrument(cello1);
        
        // Flutes
        Instrument flute1 = new Instrument("Student Flute", "Flute", "Yamaha",
            new BigDecimal("5.00"), new BigDecimal("100.00"));
        flute1.setCondition("Excellent");
        repository.addInstrument(flute1);
        
        // Clarinets
        Instrument clarinet1 = new Instrument("Bb Clarinet", "Clarinet", "Buffet",
            new BigDecimal("6.00"), new BigDecimal("120.00"));
        clarinet1.setCondition("Good");
        repository.addInstrument(clarinet1);
    }
    
    /**
     * Initialize teachers.
     */
    private void initializeTeachers() {
        // Piano teacher
        Teacher piano1 = new Teacher("Marie", "Dupont", "15 Rue de la Musique, Paris",
            LocalDate.of(1985, 3, 15), "+33 6 12 34 56 78", "marie.dupont@conservatoire.fr",
            new BigDecimal("45.00"), Arrays.asList("Piano", "Music Theory"));
        piano1.addQualification("Master in Piano Performance");
        piano1.addQualification("Certified Music Teacher");
        piano1.setYearsOfExperience(12);
        piano1.setAvailability(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(17, 0));
        piano1.setAvailability(DayOfWeek.TUESDAY, LocalTime.of(9, 0), LocalTime.of(17, 0));
        piano1.setAvailability(DayOfWeek.WEDNESDAY, LocalTime.of(9, 0), LocalTime.of(13, 0));
        piano1.setAvailability(DayOfWeek.THURSDAY, LocalTime.of(9, 0), LocalTime.of(17, 0));
        piano1.setAvailability(DayOfWeek.FRIDAY, LocalTime.of(9, 0), LocalTime.of(15, 0));
        repository.addTeacher(piano1);
        
        // Violin teacher
        Teacher violin1 = new Teacher("Jean", "Martin", "22 Avenue Mozart, Paris",
            LocalDate.of(1978, 7, 22), "+33 6 23 45 67 89", "jean.martin@conservatoire.fr",
            new BigDecimal("50.00"), Arrays.asList("Violin", "Viola"));
        violin1.addQualification("Concert Violinist");
        violin1.addQualification("PhD in Music");
        violin1.setYearsOfExperience(18);
        violin1.setAvailability(DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(18, 0));
        violin1.setAvailability(DayOfWeek.WEDNESDAY, LocalTime.of(10, 0), LocalTime.of(18, 0));
        violin1.setAvailability(DayOfWeek.FRIDAY, LocalTime.of(10, 0), LocalTime.of(16, 0));
        violin1.setAvailability(DayOfWeek.SATURDAY, LocalTime.of(9, 0), LocalTime.of(13, 0));
        repository.addTeacher(violin1);
        
        // Guitar teacher
        Teacher guitar1 = new Teacher("Carlos", "Rodriguez", "8 Rue Beethoven, Paris",
            LocalDate.of(1990, 11, 5), "+33 6 34 56 78 90", "carlos.rodriguez@conservatoire.fr",
            new BigDecimal("40.00"), Arrays.asList("Guitar", "Bass Guitar", "Music Theory"));
        guitar1.addQualification("Bachelor in Music");
        guitar1.addQualification("Professional Guitarist");
        guitar1.setYearsOfExperience(8);
        guitar1.setAvailability(DayOfWeek.TUESDAY, LocalTime.of(14, 0), LocalTime.of(20, 0));
        guitar1.setAvailability(DayOfWeek.THURSDAY, LocalTime.of(14, 0), LocalTime.of(20, 0));
        guitar1.setAvailability(DayOfWeek.SATURDAY, LocalTime.of(10, 0), LocalTime.of(18, 0));
        repository.addTeacher(guitar1);
        
        // Drums teacher
        Teacher drums1 = new Teacher("Sophie", "Bernard", "45 Rue des Arts, Paris",
            LocalDate.of(1988, 4, 12), "+33 6 45 67 89 01", "sophie.bernard@conservatoire.fr",
            new BigDecimal("42.00"), Arrays.asList("Drums", "Percussion"));
        drums1.addQualification("Professional Drummer");
        drums1.addQualification("Jazz Ensemble Experience");
        drums1.setYearsOfExperience(10);
        drums1.setAvailability(DayOfWeek.MONDAY, LocalTime.of(14, 0), LocalTime.of(20, 0));
        drums1.setAvailability(DayOfWeek.WEDNESDAY, LocalTime.of(14, 0), LocalTime.of(20, 0));
        drums1.setAvailability(DayOfWeek.FRIDAY, LocalTime.of(14, 0), LocalTime.of(20, 0));
        repository.addTeacher(drums1);
        
        // Flute teacher
        Teacher flute1 = new Teacher("Elena", "Petrov", "30 Boulevard Chopin, Paris",
            LocalDate.of(1982, 9, 28), "+33 6 56 78 90 12", "elena.petrov@conservatoire.fr",
            new BigDecimal("48.00"), Arrays.asList("Flute", "Clarinet", "Music Theory"));
        flute1.addQualification("Master in Wind Instruments");
        flute1.addQualification("Orchestra Experience");
        flute1.setYearsOfExperience(15);
        flute1.setAvailability(DayOfWeek.TUESDAY, LocalTime.of(9, 0), LocalTime.of(17, 0));
        flute1.setAvailability(DayOfWeek.THURSDAY, LocalTime.of(9, 0), LocalTime.of(17, 0));
        flute1.setAvailability(DayOfWeek.SATURDAY, LocalTime.of(9, 0), LocalTime.of(12, 0));
        repository.addTeacher(flute1);
    }
    
    /**
     * Initialize students.
     */
    private void initializeStudents() {
        // Adult students
        Student student1 = new Student("Alice", "Moreau", "10 Rue de la Paix, Paris",
            LocalDate.of(1995, 6, 20), "+33 6 11 22 33 44", "alice.moreau@email.com", Level.INTERMEDIATE);
        student1.addPreferredInstrument("Piano");
        repository.addStudent(student1);
        
        Student student2 = new Student("Thomas", "Leroy", "25 Avenue Foch, Paris",
            LocalDate.of(1998, 2, 14), "+33 6 22 33 44 55", "thomas.leroy@email.com", Level.BEGINNER);
        student2.addPreferredInstrument("Guitar");
        student2.addPreferredInstrument("Bass Guitar");
        repository.addStudent(student2);
        
        Student student3 = new Student("Emma", "Dubois", "5 Rue Victor Hugo, Paris",
            LocalDate.of(1992, 8, 8), "+33 6 33 44 55 66", "emma.dubois@email.com", Level.ADVANCED);
        student3.addPreferredInstrument("Violin");
        repository.addStudent(student3);
        
        Student student4 = new Student("Lucas", "Simon", "18 Boulevard Haussmann, Paris",
            LocalDate.of(2000, 12, 1), "+33 6 44 55 66 77", "lucas.simon@email.com", Level.INTERMEDIATE);
        student4.addPreferredInstrument("Drums");
        student4.addPreferredInstrument("Percussion");
        repository.addStudent(student4);
        
        Student student5 = new Student("Chloé", "Laurent", "42 Rue de Rivoli, Paris",
            LocalDate.of(1997, 4, 25), "+33 6 55 66 77 88", "chloe.laurent@email.com", Level.BEGINNER);
        student5.addPreferredInstrument("Flute");
        repository.addStudent(student5);
        
        // Minor students
        Student student6 = new Student("Hugo", "Petit", "33 Avenue des Champs, Paris",
            LocalDate.of(2010, 9, 15), "+33 6 66 77 88 99", "petit.family@email.com", Level.BEGINNER);
        student6.addPreferredInstrument("Piano");
        student6.setParentGuardianName("Marc Petit");
        student6.setParentGuardianPhone("+33 6 77 88 99 00");
        repository.addStudent(student6);
        
        Student student7 = new Student("Léa", "Girard", "12 Rue Mozart, Paris",
            LocalDate.of(2012, 3, 7), "+33 6 77 88 99 00", "girard.family@email.com", Level.BEGINNER);
        student7.addPreferredInstrument("Violin");
        student7.setParentGuardianName("Anne Girard");
        student7.setParentGuardianPhone("+33 6 88 99 00 11");
        repository.addStudent(student7);
        
        Student student8 = new Student("Nathan", "Roux", "56 Avenue Opera, Paris",
            LocalDate.of(2008, 11, 30), "+33 6 88 99 00 11", "roux.family@email.com", Level.INTERMEDIATE);
        student8.addPreferredInstrument("Guitar");
        student8.setParentGuardianName("Pierre Roux");
        student8.setParentGuardianPhone("+33 6 99 00 11 22");
        repository.addStudent(student8);
    }
    
    /**
     * Initialize course packages.
     */
    private void initializePackages() {
        List<Student> students = repository.getAllStudents();
        List<Room> rooms = repository.getAllRooms();
        
        if (students.size() < 5) return;
        
        // Package for Alice (Piano)
        CoursePackage pkg1 = new CoursePackage("Piano Intermediate Package", 
            new BigDecimal("450.00"), students.get(0).getId(),
            10, "Piano", LocalDate.now(), LocalDate.now().plusMonths(3));
        pkg1.setPaid(true);
        students.get(0).addPackageHours(pkg1.getId(), 10);
        repository.addService(pkg1);
        
        // Package for Thomas (Guitar)
        CoursePackage pkg2 = new CoursePackage("Guitar Beginner Package",
            new BigDecimal("350.00"), students.get(1).getId(),
            8, "Guitar", LocalDate.now(), LocalDate.now().plusMonths(2));
        pkg2.setPaid(true);
        students.get(1).addPackageHours(pkg2.getId(), 8);
        repository.addService(pkg2);
        
        // Unlimited package for Emma (Violin)
        CoursePackage pkg3 = new CoursePackage("Violin Unlimited",
            new BigDecimal("800.00"), students.get(2).getId(),
            "Violin", LocalDate.now(), LocalDate.now().plusMonths(6), 3);
        pkg3.setPaid(true);
        repository.addService(pkg3);
        
        // Package for Lucas (Drums)
        CoursePackage pkg4 = new CoursePackage("Drums Intermediate Package",
            new BigDecimal("400.00"), students.get(3).getId(),
            8, "Drums", LocalDate.now(), LocalDate.now().plusMonths(2));
        pkg4.setPaid(false); // Not yet paid
        students.get(3).addPackageHours(pkg4.getId(), 8);
        repository.addService(pkg4);
        
        // Package for Hugo (Piano - minor)
        CoursePackage pkg5 = new CoursePackage("Piano Kids Package",
            new BigDecimal("300.00"), students.get(5).getId(),
            12, "Piano", LocalDate.now(), LocalDate.now().plusMonths(4));
        pkg5.setPaid(true);
        students.get(5).addPackageHours(pkg5.getId(), 12);
        repository.addService(pkg5);
        
        // Individual lesson for Chloé
        IndividualLesson ind1 = new IndividualLesson("Single Flute Lesson",
            new BigDecimal("55.00"), students.get(4).getId(),
            "Flute", 60, null);
        ind1.setPaid(true);
        repository.addService(ind1);
        
        // Instrument rental for Thomas
        List<Instrument> guitars = repository.getInstrumentsByType("Guitar");
        if (!guitars.isEmpty()) {
            Instrument guitar = guitars.get(0);
            InstrumentRental rental = new InstrumentRental(
                students.get(1).getId(), guitar.getId(), guitar.getName(),
                guitar.getDailyRentalRate(), guitar.getDepositRequired(),
                LocalDate.now(), LocalDate.now().plusMonths(1));
            rental.setDepositPaid(true);
            rental.setPaid(true);
            guitar.rentTo(students.get(1).getId());
            repository.addService(rental);
        }
    }
    
    /**
     * Initialize scheduled lessons.
     */
    private void initializeLessons() {
        List<Student> students = repository.getAllStudents();
        List<Teacher> teachers = repository.getAllTeachers();
        List<Room> rooms = repository.getAllRooms();
        
        if (students.isEmpty() || teachers.isEmpty() || rooms.isEmpty()) return;
        
        // Find specific teachers and rooms
        Teacher pianoTeacher = teachers.stream()
            .filter(t -> t.canTeach("Piano")).findFirst().orElse(null);
        Teacher violinTeacher = teachers.stream()
            .filter(t -> t.canTeach("Violin")).findFirst().orElse(null);
        Teacher guitarTeacher = teachers.stream()
            .filter(t -> t.canTeach("Guitar")).findFirst().orElse(null);
        
        Room pianoRoom = rooms.stream()
            .filter(r -> r.isSuitableFor("Piano")).findFirst().orElse(null);
        Room stringRoom = rooms.stream()
            .filter(r -> r.isSuitableFor("Violin")).findFirst().orElse(null);
        Room guitarRoom = rooms.stream()
            .filter(r -> r.isSuitableFor("Guitar")).findFirst().orElse(null);
        
        // Create lessons for next week
        LocalDateTime nextMonday = LocalDate.now()
            .with(java.time.temporal.TemporalAdjusters.next(DayOfWeek.MONDAY))
            .atTime(10, 0);
        
        // Piano lesson for Alice
        if (pianoTeacher != null && pianoRoom != null) {
            Lesson lesson1 = new Lesson(nextMonday, Duration.ofHours(1), pianoRoom.getId(),
                pianoTeacher.getId(), students.get(0).getId(), "Piano");
            repository.addScheduledActivity(lesson1);
            
            // Piano lesson for Hugo (minor)
            if (students.size() > 5) {
                Lesson lesson5 = new Lesson(nextMonday.plusHours(2), Duration.ofMinutes(45), 
                    pianoRoom.getId(), pianoTeacher.getId(), students.get(5).getId(), "Piano");
                repository.addScheduledActivity(lesson5);
            }
        }
        
        // Violin lesson for Emma
        if (violinTeacher != null && stringRoom != null) {
            LocalDateTime wednesday = nextMonday.plusDays(2);
            Lesson lesson2 = new Lesson(wednesday, Duration.ofHours(1), stringRoom.getId(),
                violinTeacher.getId(), students.get(2).getId(), "Violin");
            repository.addScheduledActivity(lesson2);
        }
        
        // Guitar lesson for Thomas
        if (guitarTeacher != null && guitarRoom != null) {
            LocalDateTime tuesday = nextMonday.plusDays(1).withHour(15);
            Lesson lesson3 = new Lesson(tuesday, Duration.ofHours(1), guitarRoom.getId(),
                guitarTeacher.getId(), students.get(1).getId(), "Guitar");
            repository.addScheduledActivity(lesson3);
        }
        
        // Group lesson example
        if (violinTeacher != null && stringRoom != null && students.size() > 6) {
            LocalDateTime saturday = nextMonday.plusDays(5).withHour(10);
            Lesson groupLesson = new Lesson(saturday, Duration.ofHours(1), stringRoom.getId(),
                violinTeacher.getId(), 
                Arrays.asList(students.get(2).getId(), students.get(6).getId()),
                "Violin");
            groupLesson.setGroupLesson(true);
            repository.addScheduledActivity(groupLesson);
        }
    }
    
    /**
     * Initialize exams.
     */
    private void initializeExams() {
        LocalDateTime examDate1 = LocalDateTime.now().plusMonths(2).withHour(9).withMinute(0);
        LocalDateTime examDate2 = LocalDateTime.now().plusMonths(3).withHour(14).withMinute(0);
        
        // Piano Grade 3 Exam
        Exam exam1 = examService.createExam("Piano Grade 3 Examination", "Piano",
            examDate1, 15, new BigDecimal("75.00"));
        exam1.setDescription("Official ABRSM Grade 3 Piano Examination");
        exam1.setDurationMinutes(15);
        List<Room> rooms = repository.getAllRooms();
        if (!rooms.isEmpty()) {
            exam1.setRoomId(rooms.get(0).getId());
        }
        
        // Violin Grade 2 Exam
        Exam exam2 = examService.createExam("Violin Grade 2 Examination", "Violin",
            examDate2, 12, new BigDecimal("65.00"));
        exam2.setDescription("Official ABRSM Grade 2 Violin Examination");
        exam2.setDurationMinutes(12);
        
        // Guitar Grade 1 Exam  
        Exam exam3 = examService.createExam("Guitar Grade 1 Examination", "Guitar",
            examDate1.plusDays(7), 20, new BigDecimal("55.00"));
        exam3.setDescription("Beginner Guitar Certification Exam");
        exam3.setDurationMinutes(10);
        
        // Music Theory Exam
        Exam exam4 = examService.createExam("Music Theory Level 1", "Music Theory",
            examDate2.plusDays(14), 25, new BigDecimal("45.00"));
        exam4.setDescription("Written examination covering basic music theory");
        exam4.setDurationMinutes(60);
        
        // Register some students for exams
        List<Student> students = repository.getAllStudents();
        if (students.size() >= 5) {
            // Register Alice for Piano exam
            examService.registerStudentForExam(exam1.getId(), students.get(0).getId());
            
            // Register Emma for Violin exam
            examService.registerStudentForExam(exam2.getId(), students.get(2).getId());
            
            // Register Thomas for Guitar exam
            examService.registerStudentForExam(exam3.getId(), students.get(1).getId());
        }
        
        // Past exam with results
        Exam pastExam = new Exam("Piano Grade 2 (Completed)", "Piano",
            LocalDateTime.now().minusMonths(1), 10, new BigDecimal("70.00"));
        pastExam.closeRegistration();
        repository.addExam(pastExam);
        
        if (students.size() >= 6) {
            pastExam.registerStudent(students.get(0).getId());
            pastExam.recordResult(students.get(0).getId(), ExamResult.PASS, 85);
            
            pastExam.registerStudent(students.get(5).getId());
            pastExam.recordResult(students.get(5).getId(), ExamResult.DISTINCTION, 92);
        }
    }
    
    /**
     * Initialize sample payments.
     */
    private void initializePayments() {
        List<Student> students = repository.getAllStudents();
        
        if (students.size() >= 4) {
            // Payment for Alice's package
            paymentService.recordPayment(students.get(0).getId(), 
                new BigDecimal("450.00"), "Card");
            
            // Payment for Thomas's package
            paymentService.recordPayment(students.get(1).getId(),
                new BigDecimal("350.00"), "Transfer");
            
            // Payment for Emma's unlimited package
            paymentService.recordPayment(students.get(2).getId(),
                new BigDecimal("800.00"), "Card");
            
            // Partial payment for Lucas's package
            paymentService.recordPayment(students.get(3).getId(),
                new BigDecimal("200.00"), "Cash");
        }
    }
}

