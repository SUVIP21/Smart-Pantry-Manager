#Screen Flow
Initial version of the application contains the following flow
```
                                 -----------------
                                    Pantry List
                                    MainActivity
                                 -----------------
                                         |
                                         |
           ------------------------------------------------------------
           |                                                          |
           |                                                          |
----------------                                              ------------------
Add Ingredient                                                  Edit Ingredient
AddEditActivity                                                 AddEditActivity
----------------                                              -------------------
           |                                                          |
           |                                                          |
           ------------------------------------------------------------
                                          |
                                  ---------------------
                                      SQLite DB
                                      SmartPantry.db
                                  ---------------------
                                          |
                                  ---------------------
                                       Pantry List
                                  ---------------------

Long press on pantry item -> Delete Dialog -> Delete from DB
```
#Current Screens
Pantry List: Displays all pantry ingredients retrieved from the SQLite database
Add/Edit Ingredient: Allows user to create a new pantry item or modify an existing pantry item
Delete: The user can long-press an ingredient and confirm deletion

#Future Screens
Application will later include Suggested Recipes, Recipe Detail, Settings