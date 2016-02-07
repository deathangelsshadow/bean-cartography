Bean Cartography
----------------

Dependencies:
-Java 1.8 or later
-Maven (3.3.3 or later recommended)
-Active internet connection (to download Maven dependencies)

Purpose:
This project was created to ease the burden on developers of mapping fields from one class to another.  This project will
automatically do this mapping (even across classes and types) by way of name annotations.

Use Cases:
-Assume there is a web service that parses a payload into beans.  These beans may then need to be used to populate business
 objects or Hibernate classes.  The developer must write code to move/format data both to and from the targets.

 Using Bean Cartography, the developer will need to assign "business" names to fields and objects.  Bean Cartographer will
 recognize name matches and copy the field contents automatically.


