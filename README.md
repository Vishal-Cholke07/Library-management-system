📚 Library Management System
A comprehensive desktop application built with Java for managing library operations including book inventory, user management, and automated lending workflows with smart late fee calculation.

📋 Table of Contents

Features

Tech Stack

Installation

Usage

License


✨ Features
📖 Book Management

✅ Add, update, and delete books
✅ Track ISBN, title, author, and publisher
✅ Manage total and available copies
✅ Real-time inventory tracking
✅ Search and filter functionality

👥 User Management

✅ Register library members
✅ Store contact information (email, phone)
✅ Complete CRUD operations
✅ User profile management

🔄 Issue/Return System

✅ Automated book lending workflow
✅ 14-day lending period (configurable)
✅ Track issued books with due dates
✅ Return processing with date validation
✅ Smart late fee calculation (₹5 per day)
✅ Automatic inventory updates

💾 Database Features

✅ SQLite database for data persistence
✅ Relational database design
✅ Foreign key constraints
✅ Transaction management
✅ Prepared statements for SQL injection prevention


🛠 Tech Stack
TechnologyPurposeJava 8+Core programming languageSwingGUI frameworkJDBCDatabase connectivitySQLiteEmbedded databaseSQLDatabase queries

📥 Installation
Prerequisites

Java Development Kit (JDK) 8 or higher
SQLite JDBC Driver

Step 1: Clone the Repository
bashgit clone https://github.com/Vishal-Cholke07/library-management-system.git
cd library-management-system
Step 2: Download SQLite JDBC Driver
Download the SQLite JDBC driver and place it in the lib folder:

Direct Download: sqlite-jdbc-3.44.1.0.jar
Maven Repository: Maven Central

Step 3: Project Structure
Ensure your project structure looks like this:

LibraryManagementSystem/
├── src/
   └── LibraryManagementSystem.java

├── lib/
   └── sqlite-jdbc-3.44.1.0.jar

├── bin/

Step 4: Compile the Application
bash# Create bin directory if it doesn't exist
mkdir bin

# Compile
javac -cp "lib/sqlite-jdbc-3.44.1.0.jar" -d bin src/LibraryManagementSystem.java

Step 5: Run the Application

Windows:
bashjava -cp "bin;lib/sqlite-jdbc-3.44.1.0.jar" LibraryManagementSystem

Quick Start Scripts
For Windows (run.bat):
batch@echo off
javac -cp "lib/sqlite-jdbc-3.44.1.0.jar" -d bin src/LibraryManagementSystem.java
java -cp "bin;lib/sqlite-jdbc-3.44.1.0.jar" LibraryManagementSystem
pause
Make the script executable:
bashchmod +x run.sh
./run.sh

🎯 Usage
Getting Started

Launch the Application

Run the application using one of the methods above
The database (library.db) will be created automatically on first run


Add Books

Navigate to the "Books" tab
Fill in book details (title, author, ISBN, copies)
Click "Add Book"

Register Users

Go to the "Users" tab
Enter user information
Click "Add User"

Issue Books

Switch to "Issue/Return" tab
Select a user and an available book
Click "Issue Book"
Due date is automatically set to 14 days from issue date

Return Books

Select an issued book from the table
Click "Return Selected Book"
Late fees are calculated automatically if overdue

Late Fee Calculation

Rate: ₹5 per day
Calculation: Automatic based on due date
Example: 5 days late = ₹25 late fee

When using, modifying, or distributing this software, please provide appropriate
credit to the original author:
Library Management System
Copyright (c) 2024 [Vishal Cholke]
GitHub: https://github.com/Vishal-Cholke07
/library-management-system
