# Bean Cartography

## Purpose:
This project was created to ease the burden on developers of mapping fields from one class to another.  This project will
automatically do this mapping (even across classes and types) by way of name annotations:

* NamedClass - associates a name to a class
* NamedClassComposite - used in addition to NamedClass, this annotation identifies other types from which to pull named 
fields.  These types will be preferred when searching for matching names. 
* NamedField

To accomplish this, the user needs to create a metadata (or business name) repository.  This repository should include
types as well as fields.  This repository doesn't have to be separate (but could be for maintainability purposes.)  The
annotations will serve to identify classes and fields as members of the repository.  Again, there is no requirement that
a separate repository exists.  This is merely conceptual.  

An example would be an order system where "customer", "order", and "address" would identify 
types (i.e. classes.) Names such as "first name", "home phone", and "shipping address" would identify fields that would 
be contained in one or more of the named types.  Keep in mind that fields can also contain other named types.  This would
be the case for the field named "shipping address".  That would likely refer to another type named "address".  

## Dependencies:
* Java 1.8 or later
* Maven (3.3.3 or later recommended)
* Active internet connection (to download Maven dependencies)

## Use Cases:
Assume there is a web service that parses a payload into beans.  These beans may then need to be used to populate business
objects or Hibernate classes.  The developer must write code to move/format data both to and from the targets.

Using Bean Cartography, the developer will need to assign "business" names to fields and objects.  Bean Cartographer will
recognize name matches and copy the field contents automatically.

### Supported Operations:
* Copy across homogeneous classes
* Copy across heterogeneous classes
* Copy across contained classes
* Copy across arrays, collections, and maps
* Instantiate classes in the target object that are contained in arrays, collections, and maps

### Unsupported Operations:
* Instantiation of objects contained in the target.  (excluding arrays, collections, and maps)
  *This may be supported in a future release.*

## Usage:
To identify that a class will work with the Cartographer, the "NamedClass" annotation should be used.  This identifies
the class by name to the Cartographer and indicates that it contains mappable fields.

To identify that a field will work with the Cartographer, the "NamedField" annotation should be used.  If a field needs
to be formatted or converted to a different type, a Formatter should be associated to this field by name
(e.g. cartographer.addFieldFormatter("fieldName", new Formatter() { implementation }))

Two different classes should NOT have the same name in their "NamedClass" annotation.  Well, at least this is true for
the SAME RUN.  If two like "NamedClass" annotations are found they are assumed to be the same type!

If a field needs to be converted to a different type or formatted differently, a Formatter should be created and associated
to a field by name.

Since the target (i.e. new) object map drives this process, if there are null objects in the target they will
be skipped -- even if the source has them.  Conversely, if the target has an object that the source does not (null)
then no fields will be changed on the target.


### Class-to-Class

#### Homogeneous (same type) class copy

    @NamedClass(name = "Customer")
    public class Customer {
    
        @NamedField(name = "FirstName")
        private String firstName   = null;

        @NamedField(name = "LastName")
        private String lastName    = null;
    
        @NamedField(name = "PhoneNumber")
        private String phoneNumber = null;
    
        @NamedField(name = "HomeAddress")
        private Address residences = new Address();
    
A previously-populated Customer class can have named fields copied to another instantiated, although empty, Customer class.

#### Heterogeneous (different types) class copy

    @NamedClass(name = "Customer")
    public class Customer {

        @NamedField(name = "FirstName")
        private String firstName   = null;

        @NamedField(name = "LastName")
        private String lastName    = null;

        @NamedField(name = "PhoneNumber")
        private String phoneNumber = null;

        @NamedField(name = "HomeAddress")
        private Address residences = new Address();
    
    
    @NamedClass(name = "Person")
    public class Person {
    
        @NamedField(name = "FirstName")
        private String firstName   = null;

        @NamedField(name = "LastName")
        private String lastName    = null;

        @NamedField(name = "PhoneNumber")
        private String phoneNumber = null;

        @NamedField(name = "Properties")
        private List<Address> residences = new ArrayList<>();
    
In this example, all of the fields having the same "NamedField" name will be copied.  Notice "Properties" and
"HomeAddress" have an Address type but will not be copied because of differing field names.

Contained instances will also be copied.  They may be of the same or different types.  As long as the contained
instances follow the above conventions, they will be copied.  There is currently the limitiation that the target
instance must have instantiated all of its contained classes before the fields will be copied.  This restriction will
be lifted in a future version of this application.

#### Composite Classes
Composite classes are those classes containing a NamedClassComposite annotation.  This tells the Cartographer to use
the following rules when copying fields:

1. Use matching named fields from the source class that match the "NamedClass" annotation on the target class.
1. If a composite class is named and it exists in source use its named field matches.
1. For the remaining named fields scan all source objects for a name match and if found use it (first match.)

    @NamedClass(name = "Order")
    @NamedClassComposite(names = {"Person", "Item"})
    public class Order {

### Collections, Maps, and Arrays

Use the appropriate constructor of Cartographer to turn on the processing of collections.  This will have the effect
of instantiating the elements in the target (one for each in the source.)  

The business names for the map or collection fields must be the same although their contained objects don't have to 
match.  Their types must also match (i.e. both must be maps, collections, or arrays.)
  

