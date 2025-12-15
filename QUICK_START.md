# 🚀 Quick Start Guide

## Get Started in 3 Steps!

### Step 1: Prerequisites ✓
Make sure you have installed:
- **Java 17 or higher** - [Download here](https://adoptium.net/)
- **Maven 3.6 or higher** - [Download here](https://maven.apache.org/download.cgi)

Verify installation:
```bash
java -version
mvn -version
```

### Step 2: Run the Application 🎵

#### Option A: GUI Application (Recommended)

**Windows:**
```bash
run-gui.bat
```

**Linux/Mac:**
```bash
chmod +x run-gui.sh
./run-gui.sh
```

**Or using Maven:**
```bash
mvn clean javafx:run
```

#### Option B: Console Application

**Windows:**
```bash
run-console.bat
```

**Linux/Mac:**
```bash
chmod +x run-console.sh
./run-console.sh
```

### Step 3: Load Test Data 📊

When the application starts:
1. Click **"OK"** when prompted to load test data
2. The system will initialize with sample data:
   - 8 students (including 3 minors)
   - 5 teachers with various specializations
   - 8 rooms and 7 instruments
   - Course packages and lessons
   - Scheduled activities
   - Exams with registrations
   - Payment records

## First Actions to Try 🎯

### In the GUI:

1. **📊 Dashboard** - View system statistics
   - See total students, teachers, services
   - Check revenue and activity counts

2. **👨‍🎓 Students** - Manage students
   - Click "Add Student" to create a new student
   - Click "View" on any student to see details
   - Use the search box to find students

3. **📅 Scheduling** - Schedule a lesson
   - Click "Schedule Lesson"
   - Select a teacher, student, and room
   - Choose date and time
   - Click OK to schedule

4. **💰 Payments** - Record a payment
   - Click "Record Payment"
   - Select a student
   - Enter amount and payment method
   - Click OK to save

5. **📝 Exams** - Register for an exam
   - Click on an exam row
   - Click "Register"
   - Select a student
   - Click OK to register

## Navigation 🧭

### Sidebar Menu:
- **📊 Dashboard** - Overview and statistics
- **👨‍🎓 Students** - Student management
- **👨‍🏫 Teachers** - Teacher management
- **📦 Services** - Packages, lessons, rentals
- **📅 Scheduling** - Lessons and room bookings
- **📝 Exams** - Exam management
- **💰 Payments** - Billing and payments
- **🚪 Exit** - Close application

### Action Buttons:
- **👁 View** (Blue) - View details
- **✏ Edit** (Orange) - Modify record
- **🗑 Delete** (Red) - Remove record
- **➕ Add/Create** (Green) - Add new record

## Common Tasks 📝

### Add a New Student
1. Go to **Students** panel
2. Click **"Add Student"**
3. Fill in the form:
   - First Name, Last Name
   - Address, Date of Birth
   - Phone, Email
   - Level (Beginner/Intermediate/Advanced)
   - Preferred Instrument
4. Click **OK**

### Create a Course Package
1. Go to **Services** panel
2. Click **"Course Package"**
3. Select a student
4. Enter package details:
   - Name, Price
   - Instrument
   - Total hours or unlimited
   - Duration in months
5. Click **OK**

### Schedule a Lesson
1. Go to **Scheduling** panel
2. Click **"Schedule Lesson"**
3. Select:
   - Teacher
   - Student
   - Room
   - Date and time
   - Duration
4. Click **OK**

### Record a Payment
1. Go to **Payments** panel
2. Click **"Record Payment"**
3. Select student
4. Enter amount
5. Choose payment method
6. Click **OK**

## Tips & Tricks 💡

1. **Use Filters**: Most tables have filters to quickly find records
2. **Today Button**: In Scheduling, click "Today" to see today's activities
3. **Search**: Use search boxes to find students/teachers by name or ID
4. **Refresh**: Click refresh button to update data after changes
5. **Details**: Always use "View" button to see complete information

## Troubleshooting 🔧

### Application won't start
```bash
# Clean and rebuild
mvn clean install

# Then try running again
mvn javafx:run
```

### JavaFX not found
- Ensure Java 17+ is installed
- On some systems, install JavaFX SDK separately
- Set PATH_TO_FX environment variable if needed

### No data showing
- Make sure you clicked "OK" to load test data
- Or manually add data through the GUI

### Port/Permission errors
- Close any other instances of the application
- Run as administrator (Windows) or with sudo (Linux/Mac) if needed

## Need Help? 📚

- **Full Documentation**: See [README.md](README.md)
- **GUI Guide**: See [GUI_README.md](GUI_README.md)
- **Implementation Details**: See [GUI_IMPLEMENTATION_SUMMARY.md](GUI_IMPLEMENTATION_SUMMARY.md)

## What's Next? 🎓

After getting familiar with the basics:

1. **Explore OOP Concepts**: Check the console app's option 6
2. **Try Advanced Features**: 
   - Group lessons
   - Unlimited packages
   - Exam statistics
   - Monthly reports
3. **Customize**: Modify the CSS for different themes
4. **Extend**: Add your own features using the existing architecture

---

**Enjoy using Conservatoire Virtuel!** 🎵

For questions or issues, refer to the documentation or check the code comments.

