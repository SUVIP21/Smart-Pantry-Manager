#Smart Pantry Manager
#Database Design
#Database Technology
Smart Pantry Manager uses SQLite as it's local database.
The database is implemented using Android's SQLiteOpenHelper class.

#Choice of SQLite
SQLite was selected because the Smart Pantry Manager is designed to manage the user's own pantry data locally on the Android device.
Using SQLite allows the application to store pantry information persistently so that the data remains available when the application is closed and reopened.

#Database Name
SmartPantry.db

#Pantry Table
The application contains the following pantry table 
| Column     | Data Type | Description                            |
|------------|-----------|----------------------------------------|
| id         | INTEGER   | Primary key and automatically generated|
| name       | TEXT      | Name of the pantry ingredient          |
| quantity   | REAL      | Quantity of the ingredient             |
| unit       | TEXT      | Unit used for the quantity             |
| expiry_date| TEXT      | Optional expiry date                   |

#CRUD Operations
Database supports the four required CRUD operations
#CREATE
The 'addPantryItem()' method adds a new pantry ingredient to the SQLite database
#READ
The 'getAllPantryItems()' method retrieves all pantry ingredients from the database
#UPDATE
The 'updatePantryItem()' method changes an existing pantry ingredient
#DELETE
The 'deletePantryItem()' method removes a pantry ingredient from the database

#Database Class
The main database class is DatabaseHelper.java
It extends SQLiteOpenHelper
The class is responsible for creating the database, creating the pantry table, and performing the CRUD operations