package com.becker.cache;

import java.lang.reflect.*;
import java.util.*;

/**
 * This class provides static methods to derive the memory footprint of a given
 * object. These methods have only been tested against Sun's jvm prior to jdk 1.4
 * on a Windows platform. Results may vary with other VM's or other platforms.
 */
public class ObjectSizer
{
  public static final int EXCLUDE_INTERN = 0x0001;

  //
  // Every object starts with a header.
  //
  private static final int OBJECT_HEADER_SIZE = 8;

  //
  // Size of word and double word data types when represented as a field in
  // an object. Expressed in bytes.
  //
  private static final int DOUBLE_WORD_SIZE = 8;
  private static final int WORD_SIZE        = 4;
  private static final int OBJECT_REF_SIZE  = WORD_SIZE;

  //
  // Size of the individual primitive data types when reprsented as an element
  // of an array. Expressed in bytes.
  //
  private static final int BOOLEAN_ARRAY_SIZE   = 1;
  private static final int BYTE_ARRAY_SIZE      = 1;
  private static final int CHAR_ARRAY_SIZE      = 2;
  private static final int SHORT_ARRAY_SIZE     = 2;
  private static final int FLOAT_ARRAY_SIZE     = 4;
  private static final int INT_ARRAY_SIZE       = 4;
  private static final int REF_ARRAY_SIZE       = 4;
  private static final int LONG_ARRAY_SIZE      = 8;
  private static final int DOUBLE_ARRAY_SIZE    = 8;


  private ObjectSizer() {};

  /**
   * computes the size of a given object. does not follow the references that
   * the object contains
   * Limitations: I'm not sure how this works against Array objects.
   *
   * @param obj object to size
   *
   * @return size of the object in bytes
   */
  public static int shallowSizeOf( Object obj )
  {
    int i;
    Class objClassReference;    // class reference for the target object
    Class typeClass;            // temporary class reference
    Field[] classFields;

    //
    // Objects start with an 8 byte header.
    //
    int size = OBJECT_HEADER_SIZE;

    //
    // navigate through the class hiearchy of the given object, starting at the
    // current class definition
    //
    for( objClassReference = obj.getClass();
         objClassReference != null;
         objClassReference = objClassReference.getSuperclass() )
    {
      //
      // count how many non-static fields are encountered. Static fields are
      // stored with the Class object, and therefore do not add to the size of
      // the instance object.
      //
      classFields = objClassReference.getDeclaredFields();
      for( i = 0; i < classFields.length; i++ )
      {
        if( Modifier.isStatic(classFields[i].getModifiers()) )
          continue;

        //
        // There are only 2 groupings of fields for the purposes of memory
        // allocation: those fields that require 4 bytes, and those that require
        // 8 bytes. longs and doubles take 8 bytes, everything else takes 4.
        //
        typeClass = classFields[i].getType();
        if( typeClass.isPrimitive() )
        {
          if ( typeClass == Double.TYPE || typeClass == Long.TYPE )
            size += DOUBLE_WORD_SIZE;
          else
            size += WORD_SIZE;
        }
        else
        {
          size += OBJECT_REF_SIZE;
        }
      }
    }

    return(size);
  }

  /**
   * Calculates the total memory footprint of a given object. This method
   * traverses the enitre object graph. Objects that are referred to multiple
   * times by the given object (i.e., a reference to an array containing the
   * same object reference) will only be counted once.
   * <p>
   * LIMITATIONS: This method does NOT subtract out objects in the object graph
   * which are shared with other objects (objects not referencable by the
   * given object). For example, if an immutable String object is refered to by
   * several objects including the target object, the String will still be
   * counted in the memory calculation for the target object. Therefore, if the
   * target object is garbage collected, one cannot gaurantee that the total
   * memory footprint of the target object will be reclaimed.
   *
   * @param targetObj object to size
   *
   * @return size of the target object in bytes
   */
  public static int deepSizeOf( Object targetObj )
  {
    return deepSizeOf( targetObj, 0 );
  }

  /**
   * Calculates the total memory footprint of a given object. This method
   * traverses the enitre object graph. Objects that are referred to multiple
   * times by the given object (i.e., a reference to an array containing the
   * same object reference) will only be counted once.
   * <p>
   * LIMITATIONS: This method does NOT subtract out objects in the object graph
   * which are shared with other objects (objects not referencable by the
   * given object). For example, if an immutable String object is refered to by
   * several objects including the target object, the String will still be
   * counted in the memory calculation for the target object. Therefore, if the
   * target object is garbage collected, one cannot gaurantee that the total
   * memory footprint of the target object will be reclaimed.
   *
   * @param targetObj object to size
   * @param flags to control sizing functionality
   *
   * @return size of the target object in bytes
   */
  public static int deepSizeOf( Object targetObj, int flags )
  {
    int arrayLength;
    int classSize;    // size of individual class navigated
    int iField;           // iterator for the class fields
    int iArray;           // iterator for the array elements
    int totalSize = 0;
    int iNode;
    int iNodeSize;
    int fieldModifiers;
    boolean nodeFound;
    Class classType;
    Class currentClass;   // current node in class hiearchy
    Field currentField;   // current field in class hiarchy
    Field[] classFields;  // all the fields in the current class hiearchy
    Object currentObject; // current node in object graph
    Object fieldValue;
    LinkedList objectStack;    // stack used to traverse the object graph
    List nodeHistory;   // stores all the nodes navigated
    Method sizeableMethod;
    String[] excludedFields;

    // flags to control functionality
    boolean excludeIntern = (flags & EXCLUDE_INTERN) != 0;

    boolean printClass = false;
    boolean printField = false;
    boolean printIntern = false;

    // Seed object stack
    objectStack = new LinkedList();
    objectStack.add(targetObj);
    nodeHistory = new ArrayList();

    // traverse the object graph
    while( !objectStack.isEmpty() )
    {
      currentObject = objectStack.removeLast();
      if( currentObject == null )
        continue;

      // ignore multiple references to the same object
      iNodeSize = nodeHistory.size();
      nodeFound = false;
      for ( iNode = 0; iNode < iNodeSize; ++iNode )
      {
        if ( currentObject == nodeHistory.get(iNode) )
        {
          nodeFound = true;
          break;
        }
      }

      if (nodeFound) {
          continue;
      }
      nodeHistory.add(currentObject);


      currentClass = currentObject.getClass();

      // optionally exclude interned strings
      if ( excludeIntern && String.class.isAssignableFrom(currentClass) )
      {
        // check if the string reference equals an interned reference
        // (this has the unfortunate side effect of adding uninterned
        // strings to the pool)
        if ( currentObject == ((String) currentObject).intern() )
        {
          if ( printIntern )
              System.out.println("### interned: " + currentObject);
          continue;
        }
      }

      if( printClass )
        System.out.println("### class: " + currentClass.getName());

      //
      // Objects start with an 8 byte header.
      //
      classSize = OBJECT_HEADER_SIZE;

      // treat arrays specially
      if( currentClass.isArray() )
      {
        arrayLength = Array.getLength(currentObject);
        classType = currentClass.getComponentType();

        //
        // The size of the array is (num slots) * (size of each slot).
        //

        if( classType.isPrimitive() )
        {
          if( classType == Double.TYPE )
            classSize += (DOUBLE_ARRAY_SIZE * arrayLength);
          else if( classType == Long.TYPE )
            classSize += (LONG_ARRAY_SIZE * arrayLength);
          else if( classType == Integer.TYPE )
            classSize += (INT_ARRAY_SIZE * arrayLength);
          else if( classType == Float.TYPE )
            classSize += (FLOAT_ARRAY_SIZE * arrayLength);
          else if( classType == Character.TYPE )
            classSize += (CHAR_ARRAY_SIZE * arrayLength);
          else if( classType == Short.TYPE )
            classSize += (SHORT_ARRAY_SIZE * arrayLength);
          else if( classType == Byte.TYPE )
            classSize += (BYTE_ARRAY_SIZE * arrayLength);
          else if( classType == Boolean.TYPE )
            classSize += (BOOLEAN_ARRAY_SIZE * arrayLength);
        }
        else
        {
          // Push all element of the array on to the stack so that they will
          // be navigated
          for( iArray = 0; iArray < arrayLength; iArray++ )
            objectStack.add(Array.get(currentObject, iArray));

          classSize += (REF_ARRAY_SIZE * arrayLength);
        }
      }
      else
      {
        //
        // traverse the class hiearchy of the current node
        //
        for ( ;currentClass != null; currentClass = currentClass.getSuperclass() )
        {
          // determine if this class implements the Sizeable interface
          excludedFields = null;
          if (Sizeable.class.isAssignableFrom((Class)currentClass)) {
            try
            {
              // attempt to get a method that will return fields to be excluded
              // from the memory count
              Class[] paramTypes = null;
              sizeableMethod = currentClass.getDeclaredMethod("sizeableExcludedFields", paramTypes);
              sizeableMethod.setAccessible(true);
              excludedFields = (String[]) sizeableMethod.invoke(currentClass, null);
              sizeableMethod.setAccessible(false);
            }
            catch (Exception e) {
              assert false : "interface defined but method is not";
            }
          }

          //
          // traverse the fields in the current class
          //
          classFields = currentClass.getDeclaredFields();
          for( iField = 0; iField < classFields.length; iField++ )
          {
            currentField = classFields[iField];
            fieldModifiers = currentField.getModifiers();

            // skip static fields
            if( Modifier.isStatic(fieldModifiers) )
              continue;

            // skip excluded fields
            if ( (excludedFields != null) && (Arrays.binarySearch(excludedFields, currentField.getName()) >= 0) )
              continue;

            if( printField )
              System.out.println("### field: " + currentField.getName());

            classType = currentField.getType();
            if( classType.isPrimitive() )
            {
              if( classType == Double.TYPE || classType == Long.TYPE )
                classSize += DOUBLE_WORD_SIZE;
              else
                classSize += WORD_SIZE;
            }
            else
            {
              // push the object back onto the stack so it will be traversed
              // through on the next pass
              try
              {
                classSize += OBJECT_REF_SIZE;
                currentField.setAccessible(true);
                fieldValue = currentField.get(currentObject);
                currentField.setAccessible(false);

                if (fieldValue == null)
                    continue;

                objectStack.add(fieldValue);
              }
              catch( IllegalAccessException e )
              {
                // This should never happen.
                System.out.println("ObjectSizer: failed attempt to read field");
              }
            }
          }
        }
      }

      totalSize += classSize;
    }
    return(totalSize);
  }

}

