/**
 * Autogenerated by Thrift Compiler (0.10.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.sensing.core.thrift.cmp.bean;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "all"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.10.0)", date = "2018-07-03")
public class FaceTemplateDB implements org.apache.thrift.TBase<FaceTemplateDB, FaceTemplateDB._Fields>, java.io.Serializable, Cloneable, Comparable<FaceTemplateDB> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("FaceTemplateDB");

  private static final org.apache.thrift.protocol.TField ID_FIELD_DESC = new org.apache.thrift.protocol.TField("id", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField IS_USED_FIELD_DESC = new org.apache.thrift.protocol.TField("is_used", org.apache.thrift.protocol.TType.I32, (short)2);
  private static final org.apache.thrift.protocol.TField TEMPLATE_DB_TYPE_FIELD_DESC = new org.apache.thrift.protocol.TField("template_db_type", org.apache.thrift.protocol.TType.I32, (short)3);
  private static final org.apache.thrift.protocol.TField TEMPLATE_DB_NAME_FIELD_DESC = new org.apache.thrift.protocol.TField("template_db_name", org.apache.thrift.protocol.TType.STRING, (short)4);
  private static final org.apache.thrift.protocol.TField TEMPLATE_DB_DESCRIPTION_FIELD_DESC = new org.apache.thrift.protocol.TField("template_db_description", org.apache.thrift.protocol.TType.STRING, (short)5);
  private static final org.apache.thrift.protocol.TField TEMPLATE_DB_SIZE_FIELD_DESC = new org.apache.thrift.protocol.TField("template_db_size", org.apache.thrift.protocol.TType.I32, (short)6);
  private static final org.apache.thrift.protocol.TField CREATE_TIME_FIELD_DESC = new org.apache.thrift.protocol.TField("create_time", org.apache.thrift.protocol.TType.I64, (short)7);
  private static final org.apache.thrift.protocol.TField TEMPLATE_DB_CAPACITY_FIELD_DESC = new org.apache.thrift.protocol.TField("template_db_capacity", org.apache.thrift.protocol.TType.I32, (short)8);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new FaceTemplateDBStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new FaceTemplateDBTupleSchemeFactory();

  public int id; // required
  public int is_used; // required
  public int template_db_type; // required
  public java.lang.String template_db_name; // required
  public java.lang.String template_db_description; // required
  public int template_db_size; // required
  public long create_time; // required
  public int template_db_capacity; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    ID((short)1, "id"),
    IS_USED((short)2, "is_used"),
    TEMPLATE_DB_TYPE((short)3, "template_db_type"),
    TEMPLATE_DB_NAME((short)4, "template_db_name"),
    TEMPLATE_DB_DESCRIPTION((short)5, "template_db_description"),
    TEMPLATE_DB_SIZE((short)6, "template_db_size"),
    CREATE_TIME((short)7, "create_time"),
    TEMPLATE_DB_CAPACITY((short)8, "template_db_capacity");

    private static final java.util.Map<java.lang.String, _Fields> byName = new java.util.HashMap<java.lang.String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // ID
          return ID;
        case 2: // IS_USED
          return IS_USED;
        case 3: // TEMPLATE_DB_TYPE
          return TEMPLATE_DB_TYPE;
        case 4: // TEMPLATE_DB_NAME
          return TEMPLATE_DB_NAME;
        case 5: // TEMPLATE_DB_DESCRIPTION
          return TEMPLATE_DB_DESCRIPTION;
        case 6: // TEMPLATE_DB_SIZE
          return TEMPLATE_DB_SIZE;
        case 7: // CREATE_TIME
          return CREATE_TIME;
        case 8: // TEMPLATE_DB_CAPACITY
          return TEMPLATE_DB_CAPACITY;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new java.lang.IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(java.lang.String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final java.lang.String _fieldName;

    _Fields(short thriftId, java.lang.String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public java.lang.String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __ID_ISSET_ID = 0;
  private static final int __IS_USED_ISSET_ID = 1;
  private static final int __TEMPLATE_DB_TYPE_ISSET_ID = 2;
  private static final int __TEMPLATE_DB_SIZE_ISSET_ID = 3;
  private static final int __CREATE_TIME_ISSET_ID = 4;
  private static final int __TEMPLATE_DB_CAPACITY_ISSET_ID = 5;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.ID, new org.apache.thrift.meta_data.FieldMetaData("id", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.IS_USED, new org.apache.thrift.meta_data.FieldMetaData("is_used", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.TEMPLATE_DB_TYPE, new org.apache.thrift.meta_data.FieldMetaData("template_db_type", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.TEMPLATE_DB_NAME, new org.apache.thrift.meta_data.FieldMetaData("template_db_name", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.TEMPLATE_DB_DESCRIPTION, new org.apache.thrift.meta_data.FieldMetaData("template_db_description", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.TEMPLATE_DB_SIZE, new org.apache.thrift.meta_data.FieldMetaData("template_db_size", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.CREATE_TIME, new org.apache.thrift.meta_data.FieldMetaData("create_time", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    tmpMap.put(_Fields.TEMPLATE_DB_CAPACITY, new org.apache.thrift.meta_data.FieldMetaData("template_db_capacity", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(FaceTemplateDB.class, metaDataMap);
  }

  public FaceTemplateDB() {
  }

  public FaceTemplateDB(
    int id,
    int is_used,
    int template_db_type,
    java.lang.String template_db_name,
    java.lang.String template_db_description,
    int template_db_size,
    long create_time,
    int template_db_capacity)
  {
    this();
    this.id = id;
    setIdIsSet(true);
    this.is_used = is_used;
    setIs_usedIsSet(true);
    this.template_db_type = template_db_type;
    setTemplate_db_typeIsSet(true);
    this.template_db_name = template_db_name;
    this.template_db_description = template_db_description;
    this.template_db_size = template_db_size;
    setTemplate_db_sizeIsSet(true);
    this.create_time = create_time;
    setCreate_timeIsSet(true);
    this.template_db_capacity = template_db_capacity;
    setTemplate_db_capacityIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public FaceTemplateDB(FaceTemplateDB other) {
    __isset_bitfield = other.__isset_bitfield;
    this.id = other.id;
    this.is_used = other.is_used;
    this.template_db_type = other.template_db_type;
    if (other.isSetTemplate_db_name()) {
      this.template_db_name = other.template_db_name;
    }
    if (other.isSetTemplate_db_description()) {
      this.template_db_description = other.template_db_description;
    }
    this.template_db_size = other.template_db_size;
    this.create_time = other.create_time;
    this.template_db_capacity = other.template_db_capacity;
  }

  public FaceTemplateDB deepCopy() {
    return new FaceTemplateDB(this);
  }

  @Override
  public void clear() {
    setIdIsSet(false);
    this.id = 0;
    setIs_usedIsSet(false);
    this.is_used = 0;
    setTemplate_db_typeIsSet(false);
    this.template_db_type = 0;
    this.template_db_name = null;
    this.template_db_description = null;
    setTemplate_db_sizeIsSet(false);
    this.template_db_size = 0;
    setCreate_timeIsSet(false);
    this.create_time = 0;
    setTemplate_db_capacityIsSet(false);
    this.template_db_capacity = 0;
  }

  public int getId() {
    return this.id;
  }

  public FaceTemplateDB setId(int id) {
    this.id = id;
    setIdIsSet(true);
    return this;
  }

  public void unsetId() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __ID_ISSET_ID);
  }

  /** Returns true if field id is set (has been assigned a value) and false otherwise */
  public boolean isSetId() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __ID_ISSET_ID);
  }

  public void setIdIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __ID_ISSET_ID, value);
  }

  public int getIs_used() {
    return this.is_used;
  }

  public FaceTemplateDB setIs_used(int is_used) {
    this.is_used = is_used;
    setIs_usedIsSet(true);
    return this;
  }

  public void unsetIs_used() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __IS_USED_ISSET_ID);
  }

  /** Returns true if field is_used is set (has been assigned a value) and false otherwise */
  public boolean isSetIs_used() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __IS_USED_ISSET_ID);
  }

  public void setIs_usedIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __IS_USED_ISSET_ID, value);
  }

  public int getTemplate_db_type() {
    return this.template_db_type;
  }

  public FaceTemplateDB setTemplate_db_type(int template_db_type) {
    this.template_db_type = template_db_type;
    setTemplate_db_typeIsSet(true);
    return this;
  }

  public void unsetTemplate_db_type() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __TEMPLATE_DB_TYPE_ISSET_ID);
  }

  /** Returns true if field template_db_type is set (has been assigned a value) and false otherwise */
  public boolean isSetTemplate_db_type() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __TEMPLATE_DB_TYPE_ISSET_ID);
  }

  public void setTemplate_db_typeIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __TEMPLATE_DB_TYPE_ISSET_ID, value);
  }

  public java.lang.String getTemplate_db_name() {
    return this.template_db_name;
  }

  public FaceTemplateDB setTemplate_db_name(java.lang.String template_db_name) {
    this.template_db_name = template_db_name;
    return this;
  }

  public void unsetTemplate_db_name() {
    this.template_db_name = null;
  }

  /** Returns true if field template_db_name is set (has been assigned a value) and false otherwise */
  public boolean isSetTemplate_db_name() {
    return this.template_db_name != null;
  }

  public void setTemplate_db_nameIsSet(boolean value) {
    if (!value) {
      this.template_db_name = null;
    }
  }

  public java.lang.String getTemplate_db_description() {
    return this.template_db_description;
  }

  public FaceTemplateDB setTemplate_db_description(java.lang.String template_db_description) {
    this.template_db_description = template_db_description;
    return this;
  }

  public void unsetTemplate_db_description() {
    this.template_db_description = null;
  }

  /** Returns true if field template_db_description is set (has been assigned a value) and false otherwise */
  public boolean isSetTemplate_db_description() {
    return this.template_db_description != null;
  }

  public void setTemplate_db_descriptionIsSet(boolean value) {
    if (!value) {
      this.template_db_description = null;
    }
  }

  public int getTemplate_db_size() {
    return this.template_db_size;
  }

  public FaceTemplateDB setTemplate_db_size(int template_db_size) {
    this.template_db_size = template_db_size;
    setTemplate_db_sizeIsSet(true);
    return this;
  }

  public void unsetTemplate_db_size() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __TEMPLATE_DB_SIZE_ISSET_ID);
  }

  /** Returns true if field template_db_size is set (has been assigned a value) and false otherwise */
  public boolean isSetTemplate_db_size() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __TEMPLATE_DB_SIZE_ISSET_ID);
  }

  public void setTemplate_db_sizeIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __TEMPLATE_DB_SIZE_ISSET_ID, value);
  }

  public long getCreate_time() {
    return this.create_time;
  }

  public FaceTemplateDB setCreate_time(long create_time) {
    this.create_time = create_time;
    setCreate_timeIsSet(true);
    return this;
  }

  public void unsetCreate_time() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __CREATE_TIME_ISSET_ID);
  }

  /** Returns true if field create_time is set (has been assigned a value) and false otherwise */
  public boolean isSetCreate_time() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __CREATE_TIME_ISSET_ID);
  }

  public void setCreate_timeIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __CREATE_TIME_ISSET_ID, value);
  }

  public int getTemplate_db_capacity() {
    return this.template_db_capacity;
  }

  public FaceTemplateDB setTemplate_db_capacity(int template_db_capacity) {
    this.template_db_capacity = template_db_capacity;
    setTemplate_db_capacityIsSet(true);
    return this;
  }

  public void unsetTemplate_db_capacity() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __TEMPLATE_DB_CAPACITY_ISSET_ID);
  }

  /** Returns true if field template_db_capacity is set (has been assigned a value) and false otherwise */
  public boolean isSetTemplate_db_capacity() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __TEMPLATE_DB_CAPACITY_ISSET_ID);
  }

  public void setTemplate_db_capacityIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __TEMPLATE_DB_CAPACITY_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, java.lang.Object value) {
    switch (field) {
    case ID:
      if (value == null) {
        unsetId();
      } else {
        setId((java.lang.Integer)value);
      }
      break;

    case IS_USED:
      if (value == null) {
        unsetIs_used();
      } else {
        setIs_used((java.lang.Integer)value);
      }
      break;

    case TEMPLATE_DB_TYPE:
      if (value == null) {
        unsetTemplate_db_type();
      } else {
        setTemplate_db_type((java.lang.Integer)value);
      }
      break;

    case TEMPLATE_DB_NAME:
      if (value == null) {
        unsetTemplate_db_name();
      } else {
        setTemplate_db_name((java.lang.String)value);
      }
      break;

    case TEMPLATE_DB_DESCRIPTION:
      if (value == null) {
        unsetTemplate_db_description();
      } else {
        setTemplate_db_description((java.lang.String)value);
      }
      break;

    case TEMPLATE_DB_SIZE:
      if (value == null) {
        unsetTemplate_db_size();
      } else {
        setTemplate_db_size((java.lang.Integer)value);
      }
      break;

    case CREATE_TIME:
      if (value == null) {
        unsetCreate_time();
      } else {
        setCreate_time((java.lang.Long)value);
      }
      break;

    case TEMPLATE_DB_CAPACITY:
      if (value == null) {
        unsetTemplate_db_capacity();
      } else {
        setTemplate_db_capacity((java.lang.Integer)value);
      }
      break;

    }
  }

  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case ID:
      return getId();

    case IS_USED:
      return getIs_used();

    case TEMPLATE_DB_TYPE:
      return getTemplate_db_type();

    case TEMPLATE_DB_NAME:
      return getTemplate_db_name();

    case TEMPLATE_DB_DESCRIPTION:
      return getTemplate_db_description();

    case TEMPLATE_DB_SIZE:
      return getTemplate_db_size();

    case CREATE_TIME:
      return getCreate_time();

    case TEMPLATE_DB_CAPACITY:
      return getTemplate_db_capacity();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case ID:
      return isSetId();
    case IS_USED:
      return isSetIs_used();
    case TEMPLATE_DB_TYPE:
      return isSetTemplate_db_type();
    case TEMPLATE_DB_NAME:
      return isSetTemplate_db_name();
    case TEMPLATE_DB_DESCRIPTION:
      return isSetTemplate_db_description();
    case TEMPLATE_DB_SIZE:
      return isSetTemplate_db_size();
    case CREATE_TIME:
      return isSetCreate_time();
    case TEMPLATE_DB_CAPACITY:
      return isSetTemplate_db_capacity();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof FaceTemplateDB)
      return this.equals((FaceTemplateDB)that);
    return false;
  }

  public boolean equals(FaceTemplateDB that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_id = true;
    boolean that_present_id = true;
    if (this_present_id || that_present_id) {
      if (!(this_present_id && that_present_id))
        return false;
      if (this.id != that.id)
        return false;
    }

    boolean this_present_is_used = true;
    boolean that_present_is_used = true;
    if (this_present_is_used || that_present_is_used) {
      if (!(this_present_is_used && that_present_is_used))
        return false;
      if (this.is_used != that.is_used)
        return false;
    }

    boolean this_present_template_db_type = true;
    boolean that_present_template_db_type = true;
    if (this_present_template_db_type || that_present_template_db_type) {
      if (!(this_present_template_db_type && that_present_template_db_type))
        return false;
      if (this.template_db_type != that.template_db_type)
        return false;
    }

    boolean this_present_template_db_name = true && this.isSetTemplate_db_name();
    boolean that_present_template_db_name = true && that.isSetTemplate_db_name();
    if (this_present_template_db_name || that_present_template_db_name) {
      if (!(this_present_template_db_name && that_present_template_db_name))
        return false;
      if (!this.template_db_name.equals(that.template_db_name))
        return false;
    }

    boolean this_present_template_db_description = true && this.isSetTemplate_db_description();
    boolean that_present_template_db_description = true && that.isSetTemplate_db_description();
    if (this_present_template_db_description || that_present_template_db_description) {
      if (!(this_present_template_db_description && that_present_template_db_description))
        return false;
      if (!this.template_db_description.equals(that.template_db_description))
        return false;
    }

    boolean this_present_template_db_size = true;
    boolean that_present_template_db_size = true;
    if (this_present_template_db_size || that_present_template_db_size) {
      if (!(this_present_template_db_size && that_present_template_db_size))
        return false;
      if (this.template_db_size != that.template_db_size)
        return false;
    }

    boolean this_present_create_time = true;
    boolean that_present_create_time = true;
    if (this_present_create_time || that_present_create_time) {
      if (!(this_present_create_time && that_present_create_time))
        return false;
      if (this.create_time != that.create_time)
        return false;
    }

    boolean this_present_template_db_capacity = true;
    boolean that_present_template_db_capacity = true;
    if (this_present_template_db_capacity || that_present_template_db_capacity) {
      if (!(this_present_template_db_capacity && that_present_template_db_capacity))
        return false;
      if (this.template_db_capacity != that.template_db_capacity)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + id;

    hashCode = hashCode * 8191 + is_used;

    hashCode = hashCode * 8191 + template_db_type;

    hashCode = hashCode * 8191 + ((isSetTemplate_db_name()) ? 131071 : 524287);
    if (isSetTemplate_db_name())
      hashCode = hashCode * 8191 + template_db_name.hashCode();

    hashCode = hashCode * 8191 + ((isSetTemplate_db_description()) ? 131071 : 524287);
    if (isSetTemplate_db_description())
      hashCode = hashCode * 8191 + template_db_description.hashCode();

    hashCode = hashCode * 8191 + template_db_size;

    hashCode = hashCode * 8191 + org.apache.thrift.TBaseHelper.hashCode(create_time);

    hashCode = hashCode * 8191 + template_db_capacity;

    return hashCode;
  }

  @Override
  public int compareTo(FaceTemplateDB other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSetId()).compareTo(other.isSetId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.id, other.id);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetIs_used()).compareTo(other.isSetIs_used());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetIs_used()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.is_used, other.is_used);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetTemplate_db_type()).compareTo(other.isSetTemplate_db_type());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTemplate_db_type()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.template_db_type, other.template_db_type);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetTemplate_db_name()).compareTo(other.isSetTemplate_db_name());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTemplate_db_name()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.template_db_name, other.template_db_name);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetTemplate_db_description()).compareTo(other.isSetTemplate_db_description());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTemplate_db_description()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.template_db_description, other.template_db_description);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetTemplate_db_size()).compareTo(other.isSetTemplate_db_size());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTemplate_db_size()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.template_db_size, other.template_db_size);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetCreate_time()).compareTo(other.isSetCreate_time());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetCreate_time()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.create_time, other.create_time);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetTemplate_db_capacity()).compareTo(other.isSetTemplate_db_capacity());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTemplate_db_capacity()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.template_db_capacity, other.template_db_capacity);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    scheme(iprot).read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    scheme(oprot).write(oprot, this);
  }

  @Override
  public java.lang.String toString() {
    java.lang.StringBuilder sb = new java.lang.StringBuilder("FaceTemplateDB(");
    boolean first = true;

    sb.append("id:");
    sb.append(this.id);
    first = false;
    if (!first) sb.append(", ");
    sb.append("is_used:");
    sb.append(this.is_used);
    first = false;
    if (!first) sb.append(", ");
    sb.append("template_db_type:");
    sb.append(this.template_db_type);
    first = false;
    if (!first) sb.append(", ");
    sb.append("template_db_name:");
    if (this.template_db_name == null) {
      sb.append("null");
    } else {
      sb.append(this.template_db_name);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("template_db_description:");
    if (this.template_db_description == null) {
      sb.append("null");
    } else {
      sb.append(this.template_db_description);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("template_db_size:");
    sb.append(this.template_db_size);
    first = false;
    if (!first) sb.append(", ");
    sb.append("create_time:");
    sb.append(this.create_time);
    first = false;
    if (!first) sb.append(", ");
    sb.append("template_db_capacity:");
    sb.append(this.template_db_capacity);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class FaceTemplateDBStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public FaceTemplateDBStandardScheme getScheme() {
      return new FaceTemplateDBStandardScheme();
    }
  }

  private static class FaceTemplateDBStandardScheme extends org.apache.thrift.scheme.StandardScheme<FaceTemplateDB> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, FaceTemplateDB struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.id = iprot.readI32();
              struct.setIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // IS_USED
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.is_used = iprot.readI32();
              struct.setIs_usedIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // TEMPLATE_DB_TYPE
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.template_db_type = iprot.readI32();
              struct.setTemplate_db_typeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // TEMPLATE_DB_NAME
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.template_db_name = iprot.readString();
              struct.setTemplate_db_nameIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // TEMPLATE_DB_DESCRIPTION
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.template_db_description = iprot.readString();
              struct.setTemplate_db_descriptionIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 6: // TEMPLATE_DB_SIZE
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.template_db_size = iprot.readI32();
              struct.setTemplate_db_sizeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 7: // CREATE_TIME
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.create_time = iprot.readI64();
              struct.setCreate_timeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 8: // TEMPLATE_DB_CAPACITY
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.template_db_capacity = iprot.readI32();
              struct.setTemplate_db_capacityIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, FaceTemplateDB struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(ID_FIELD_DESC);
      oprot.writeI32(struct.id);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(IS_USED_FIELD_DESC);
      oprot.writeI32(struct.is_used);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(TEMPLATE_DB_TYPE_FIELD_DESC);
      oprot.writeI32(struct.template_db_type);
      oprot.writeFieldEnd();
      if (struct.template_db_name != null) {
        oprot.writeFieldBegin(TEMPLATE_DB_NAME_FIELD_DESC);
        oprot.writeString(struct.template_db_name);
        oprot.writeFieldEnd();
      }
      if (struct.template_db_description != null) {
        oprot.writeFieldBegin(TEMPLATE_DB_DESCRIPTION_FIELD_DESC);
        oprot.writeString(struct.template_db_description);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(TEMPLATE_DB_SIZE_FIELD_DESC);
      oprot.writeI32(struct.template_db_size);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(CREATE_TIME_FIELD_DESC);
      oprot.writeI64(struct.create_time);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(TEMPLATE_DB_CAPACITY_FIELD_DESC);
      oprot.writeI32(struct.template_db_capacity);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class FaceTemplateDBTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public FaceTemplateDBTupleScheme getScheme() {
      return new FaceTemplateDBTupleScheme();
    }
  }

  private static class FaceTemplateDBTupleScheme extends org.apache.thrift.scheme.TupleScheme<FaceTemplateDB> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, FaceTemplateDB struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSetId()) {
        optionals.set(0);
      }
      if (struct.isSetIs_used()) {
        optionals.set(1);
      }
      if (struct.isSetTemplate_db_type()) {
        optionals.set(2);
      }
      if (struct.isSetTemplate_db_name()) {
        optionals.set(3);
      }
      if (struct.isSetTemplate_db_description()) {
        optionals.set(4);
      }
      if (struct.isSetTemplate_db_size()) {
        optionals.set(5);
      }
      if (struct.isSetCreate_time()) {
        optionals.set(6);
      }
      if (struct.isSetTemplate_db_capacity()) {
        optionals.set(7);
      }
      oprot.writeBitSet(optionals, 8);
      if (struct.isSetId()) {
        oprot.writeI32(struct.id);
      }
      if (struct.isSetIs_used()) {
        oprot.writeI32(struct.is_used);
      }
      if (struct.isSetTemplate_db_type()) {
        oprot.writeI32(struct.template_db_type);
      }
      if (struct.isSetTemplate_db_name()) {
        oprot.writeString(struct.template_db_name);
      }
      if (struct.isSetTemplate_db_description()) {
        oprot.writeString(struct.template_db_description);
      }
      if (struct.isSetTemplate_db_size()) {
        oprot.writeI32(struct.template_db_size);
      }
      if (struct.isSetCreate_time()) {
        oprot.writeI64(struct.create_time);
      }
      if (struct.isSetTemplate_db_capacity()) {
        oprot.writeI32(struct.template_db_capacity);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, FaceTemplateDB struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet incoming = iprot.readBitSet(8);
      if (incoming.get(0)) {
        struct.id = iprot.readI32();
        struct.setIdIsSet(true);
      }
      if (incoming.get(1)) {
        struct.is_used = iprot.readI32();
        struct.setIs_usedIsSet(true);
      }
      if (incoming.get(2)) {
        struct.template_db_type = iprot.readI32();
        struct.setTemplate_db_typeIsSet(true);
      }
      if (incoming.get(3)) {
        struct.template_db_name = iprot.readString();
        struct.setTemplate_db_nameIsSet(true);
      }
      if (incoming.get(4)) {
        struct.template_db_description = iprot.readString();
        struct.setTemplate_db_descriptionIsSet(true);
      }
      if (incoming.get(5)) {
        struct.template_db_size = iprot.readI32();
        struct.setTemplate_db_sizeIsSet(true);
      }
      if (incoming.get(6)) {
        struct.create_time = iprot.readI64();
        struct.setCreate_timeIsSet(true);
      }
      if (incoming.get(7)) {
        struct.template_db_capacity = iprot.readI32();
        struct.setTemplate_db_capacityIsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

