package com.sensing.core.thrift.cap.bean;
/**
 * Autogenerated by Thrift Compiler (0.10.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.10.0)", date = "2019-01-02")
public class CapFeaturesSet implements org.apache.thrift.TBase<CapFeaturesSet, CapFeaturesSet._Fields>, java.io.Serializable, Cloneable, Comparable<CapFeaturesSet> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("CapFeaturesSet");

  private static final org.apache.thrift.protocol.TField LST_PEOPLE_FIELD_DESC = new org.apache.thrift.protocol.TField("lstPeople", org.apache.thrift.protocol.TType.LIST, (short)1);
  private static final org.apache.thrift.protocol.TField LST_MOTORS_FIELD_DESC = new org.apache.thrift.protocol.TField("lstMotors", org.apache.thrift.protocol.TType.LIST, (short)2);
  private static final org.apache.thrift.protocol.TField LST_NONMOTORS_FIELD_DESC = new org.apache.thrift.protocol.TField("lstNonmotors", org.apache.thrift.protocol.TType.LIST, (short)3);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new CapFeaturesSetStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new CapFeaturesSetTupleSchemeFactory();

  public java.util.List<CapPeopleResult> lstPeople; // required
  public java.util.List<CapMotorResult> lstMotors; // required
  public java.util.List<CapNonmotorResult> lstNonmotors; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    LST_PEOPLE((short)1, "lstPeople"),
    LST_MOTORS((short)2, "lstMotors"),
    LST_NONMOTORS((short)3, "lstNonmotors");

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
        case 1: // LST_PEOPLE
          return LST_PEOPLE;
        case 2: // LST_MOTORS
          return LST_MOTORS;
        case 3: // LST_NONMOTORS
          return LST_NONMOTORS;
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
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.LST_PEOPLE, new org.apache.thrift.meta_data.FieldMetaData("lstPeople", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, CapPeopleResult.class))));
    tmpMap.put(_Fields.LST_MOTORS, new org.apache.thrift.meta_data.FieldMetaData("lstMotors", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, CapMotorResult.class))));
    tmpMap.put(_Fields.LST_NONMOTORS, new org.apache.thrift.meta_data.FieldMetaData("lstNonmotors", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, CapNonmotorResult.class))));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(CapFeaturesSet.class, metaDataMap);
  }

  public CapFeaturesSet() {
  }

  public CapFeaturesSet(
    java.util.List<CapPeopleResult> lstPeople,
    java.util.List<CapMotorResult> lstMotors,
    java.util.List<CapNonmotorResult> lstNonmotors)
  {
    this();
    this.lstPeople = lstPeople;
    this.lstMotors = lstMotors;
    this.lstNonmotors = lstNonmotors;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public CapFeaturesSet(CapFeaturesSet other) {
    if (other.isSetLstPeople()) {
      java.util.List<CapPeopleResult> __this__lstPeople = new java.util.ArrayList<CapPeopleResult>(other.lstPeople.size());
      for (CapPeopleResult other_element : other.lstPeople) {
        __this__lstPeople.add(new CapPeopleResult(other_element));
      }
      this.lstPeople = __this__lstPeople;
    }
    if (other.isSetLstMotors()) {
      java.util.List<CapMotorResult> __this__lstMotors = new java.util.ArrayList<CapMotorResult>(other.lstMotors.size());
      for (CapMotorResult other_element : other.lstMotors) {
        __this__lstMotors.add(new CapMotorResult(other_element));
      }
      this.lstMotors = __this__lstMotors;
    }
    if (other.isSetLstNonmotors()) {
      java.util.List<CapNonmotorResult> __this__lstNonmotors = new java.util.ArrayList<CapNonmotorResult>(other.lstNonmotors.size());
      for (CapNonmotorResult other_element : other.lstNonmotors) {
        __this__lstNonmotors.add(new CapNonmotorResult(other_element));
      }
      this.lstNonmotors = __this__lstNonmotors;
    }
  }

  public CapFeaturesSet deepCopy() {
    return new CapFeaturesSet(this);
  }

  @Override
  public void clear() {
    this.lstPeople = null;
    this.lstMotors = null;
    this.lstNonmotors = null;
  }

  public int getLstPeopleSize() {
    return (this.lstPeople == null) ? 0 : this.lstPeople.size();
  }

  public java.util.Iterator<CapPeopleResult> getLstPeopleIterator() {
    return (this.lstPeople == null) ? null : this.lstPeople.iterator();
  }

  public void addToLstPeople(CapPeopleResult elem) {
    if (this.lstPeople == null) {
      this.lstPeople = new java.util.ArrayList<CapPeopleResult>();
    }
    this.lstPeople.add(elem);
  }

  public java.util.List<CapPeopleResult> getLstPeople() {
    return this.lstPeople;
  }

  public CapFeaturesSet setLstPeople(java.util.List<CapPeopleResult> lstPeople) {
    this.lstPeople = lstPeople;
    return this;
  }

  public void unsetLstPeople() {
    this.lstPeople = null;
  }

  /** Returns true if field lstPeople is set (has been assigned a value) and false otherwise */
  public boolean isSetLstPeople() {
    return this.lstPeople != null;
  }

  public void setLstPeopleIsSet(boolean value) {
    if (!value) {
      this.lstPeople = null;
    }
  }

  public int getLstMotorsSize() {
    return (this.lstMotors == null) ? 0 : this.lstMotors.size();
  }

  public java.util.Iterator<CapMotorResult> getLstMotorsIterator() {
    return (this.lstMotors == null) ? null : this.lstMotors.iterator();
  }

  public void addToLstMotors(CapMotorResult elem) {
    if (this.lstMotors == null) {
      this.lstMotors = new java.util.ArrayList<CapMotorResult>();
    }
    this.lstMotors.add(elem);
  }

  public java.util.List<CapMotorResult> getLstMotors() {
    return this.lstMotors;
  }

  public CapFeaturesSet setLstMotors(java.util.List<CapMotorResult> lstMotors) {
    this.lstMotors = lstMotors;
    return this;
  }

  public void unsetLstMotors() {
    this.lstMotors = null;
  }

  /** Returns true if field lstMotors is set (has been assigned a value) and false otherwise */
  public boolean isSetLstMotors() {
    return this.lstMotors != null;
  }

  public void setLstMotorsIsSet(boolean value) {
    if (!value) {
      this.lstMotors = null;
    }
  }

  public int getLstNonmotorsSize() {
    return (this.lstNonmotors == null) ? 0 : this.lstNonmotors.size();
  }

  public java.util.Iterator<CapNonmotorResult> getLstNonmotorsIterator() {
    return (this.lstNonmotors == null) ? null : this.lstNonmotors.iterator();
  }

  public void addToLstNonmotors(CapNonmotorResult elem) {
    if (this.lstNonmotors == null) {
      this.lstNonmotors = new java.util.ArrayList<CapNonmotorResult>();
    }
    this.lstNonmotors.add(elem);
  }

  public java.util.List<CapNonmotorResult> getLstNonmotors() {
    return this.lstNonmotors;
  }

  public CapFeaturesSet setLstNonmotors(java.util.List<CapNonmotorResult> lstNonmotors) {
    this.lstNonmotors = lstNonmotors;
    return this;
  }

  public void unsetLstNonmotors() {
    this.lstNonmotors = null;
  }

  /** Returns true if field lstNonmotors is set (has been assigned a value) and false otherwise */
  public boolean isSetLstNonmotors() {
    return this.lstNonmotors != null;
  }

  public void setLstNonmotorsIsSet(boolean value) {
    if (!value) {
      this.lstNonmotors = null;
    }
  }

  public void setFieldValue(_Fields field, java.lang.Object value) {
    switch (field) {
    case LST_PEOPLE:
      if (value == null) {
        unsetLstPeople();
      } else {
        setLstPeople((java.util.List<CapPeopleResult>)value);
      }
      break;

    case LST_MOTORS:
      if (value == null) {
        unsetLstMotors();
      } else {
        setLstMotors((java.util.List<CapMotorResult>)value);
      }
      break;

    case LST_NONMOTORS:
      if (value == null) {
        unsetLstNonmotors();
      } else {
        setLstNonmotors((java.util.List<CapNonmotorResult>)value);
      }
      break;

    }
  }

  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case LST_PEOPLE:
      return getLstPeople();

    case LST_MOTORS:
      return getLstMotors();

    case LST_NONMOTORS:
      return getLstNonmotors();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case LST_PEOPLE:
      return isSetLstPeople();
    case LST_MOTORS:
      return isSetLstMotors();
    case LST_NONMOTORS:
      return isSetLstNonmotors();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof CapFeaturesSet)
      return this.equals((CapFeaturesSet)that);
    return false;
  }

  public boolean equals(CapFeaturesSet that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_lstPeople = true && this.isSetLstPeople();
    boolean that_present_lstPeople = true && that.isSetLstPeople();
    if (this_present_lstPeople || that_present_lstPeople) {
      if (!(this_present_lstPeople && that_present_lstPeople))
        return false;
      if (!this.lstPeople.equals(that.lstPeople))
        return false;
    }

    boolean this_present_lstMotors = true && this.isSetLstMotors();
    boolean that_present_lstMotors = true && that.isSetLstMotors();
    if (this_present_lstMotors || that_present_lstMotors) {
      if (!(this_present_lstMotors && that_present_lstMotors))
        return false;
      if (!this.lstMotors.equals(that.lstMotors))
        return false;
    }

    boolean this_present_lstNonmotors = true && this.isSetLstNonmotors();
    boolean that_present_lstNonmotors = true && that.isSetLstNonmotors();
    if (this_present_lstNonmotors || that_present_lstNonmotors) {
      if (!(this_present_lstNonmotors && that_present_lstNonmotors))
        return false;
      if (!this.lstNonmotors.equals(that.lstNonmotors))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSetLstPeople()) ? 131071 : 524287);
    if (isSetLstPeople())
      hashCode = hashCode * 8191 + lstPeople.hashCode();

    hashCode = hashCode * 8191 + ((isSetLstMotors()) ? 131071 : 524287);
    if (isSetLstMotors())
      hashCode = hashCode * 8191 + lstMotors.hashCode();

    hashCode = hashCode * 8191 + ((isSetLstNonmotors()) ? 131071 : 524287);
    if (isSetLstNonmotors())
      hashCode = hashCode * 8191 + lstNonmotors.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(CapFeaturesSet other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSetLstPeople()).compareTo(other.isSetLstPeople());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetLstPeople()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.lstPeople, other.lstPeople);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetLstMotors()).compareTo(other.isSetLstMotors());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetLstMotors()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.lstMotors, other.lstMotors);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetLstNonmotors()).compareTo(other.isSetLstNonmotors());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetLstNonmotors()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.lstNonmotors, other.lstNonmotors);
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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("CapFeaturesSet(");
    boolean first = true;

    sb.append("lstPeople:");
    if (this.lstPeople == null) {
      sb.append("null");
    } else {
      sb.append(this.lstPeople);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("lstMotors:");
    if (this.lstMotors == null) {
      sb.append("null");
    } else {
      sb.append(this.lstMotors);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("lstNonmotors:");
    if (this.lstNonmotors == null) {
      sb.append("null");
    } else {
      sb.append(this.lstNonmotors);
    }
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
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class CapFeaturesSetStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public CapFeaturesSetStandardScheme getScheme() {
      return new CapFeaturesSetStandardScheme();
    }
  }

  private static class CapFeaturesSetStandardScheme extends org.apache.thrift.scheme.StandardScheme<CapFeaturesSet> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, CapFeaturesSet struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // LST_PEOPLE
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list8 = iprot.readListBegin();
                struct.lstPeople = new java.util.ArrayList<CapPeopleResult>(_list8.size);
                CapPeopleResult _elem9;
                for (int _i10 = 0; _i10 < _list8.size; ++_i10)
                {
                  _elem9 = new CapPeopleResult();
                  _elem9.read(iprot);
                  struct.lstPeople.add(_elem9);
                }
                iprot.readListEnd();
              }
              struct.setLstPeopleIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // LST_MOTORS
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list11 = iprot.readListBegin();
                struct.lstMotors = new java.util.ArrayList<CapMotorResult>(_list11.size);
                CapMotorResult _elem12;
                for (int _i13 = 0; _i13 < _list11.size; ++_i13)
                {
                  _elem12 = new CapMotorResult();
                  _elem12.read(iprot);
                  struct.lstMotors.add(_elem12);
                }
                iprot.readListEnd();
              }
              struct.setLstMotorsIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // LST_NONMOTORS
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list14 = iprot.readListBegin();
                struct.lstNonmotors = new java.util.ArrayList<CapNonmotorResult>(_list14.size);
                CapNonmotorResult _elem15;
                for (int _i16 = 0; _i16 < _list14.size; ++_i16)
                {
                  _elem15 = new CapNonmotorResult();
                  _elem15.read(iprot);
                  struct.lstNonmotors.add(_elem15);
                }
                iprot.readListEnd();
              }
              struct.setLstNonmotorsIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, CapFeaturesSet struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.lstPeople != null) {
        oprot.writeFieldBegin(LST_PEOPLE_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, struct.lstPeople.size()));
          for (CapPeopleResult _iter17 : struct.lstPeople)
          {
            _iter17.write(oprot);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      if (struct.lstMotors != null) {
        oprot.writeFieldBegin(LST_MOTORS_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, struct.lstMotors.size()));
          for (CapMotorResult _iter18 : struct.lstMotors)
          {
            _iter18.write(oprot);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      if (struct.lstNonmotors != null) {
        oprot.writeFieldBegin(LST_NONMOTORS_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, struct.lstNonmotors.size()));
          for (CapNonmotorResult _iter19 : struct.lstNonmotors)
          {
            _iter19.write(oprot);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class CapFeaturesSetTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public CapFeaturesSetTupleScheme getScheme() {
      return new CapFeaturesSetTupleScheme();
    }
  }

  private static class CapFeaturesSetTupleScheme extends org.apache.thrift.scheme.TupleScheme<CapFeaturesSet> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, CapFeaturesSet struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSetLstPeople()) {
        optionals.set(0);
      }
      if (struct.isSetLstMotors()) {
        optionals.set(1);
      }
      if (struct.isSetLstNonmotors()) {
        optionals.set(2);
      }
      oprot.writeBitSet(optionals, 3);
      if (struct.isSetLstPeople()) {
        {
          oprot.writeI32(struct.lstPeople.size());
          for (CapPeopleResult _iter20 : struct.lstPeople)
          {
            _iter20.write(oprot);
          }
        }
      }
      if (struct.isSetLstMotors()) {
        {
          oprot.writeI32(struct.lstMotors.size());
          for (CapMotorResult _iter21 : struct.lstMotors)
          {
            _iter21.write(oprot);
          }
        }
      }
      if (struct.isSetLstNonmotors()) {
        {
          oprot.writeI32(struct.lstNonmotors.size());
          for (CapNonmotorResult _iter22 : struct.lstNonmotors)
          {
            _iter22.write(oprot);
          }
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, CapFeaturesSet struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet incoming = iprot.readBitSet(3);
      if (incoming.get(0)) {
        {
          org.apache.thrift.protocol.TList _list23 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
          struct.lstPeople = new java.util.ArrayList<CapPeopleResult>(_list23.size);
          CapPeopleResult _elem24;
          for (int _i25 = 0; _i25 < _list23.size; ++_i25)
          {
            _elem24 = new CapPeopleResult();
            _elem24.read(iprot);
            struct.lstPeople.add(_elem24);
          }
        }
        struct.setLstPeopleIsSet(true);
      }
      if (incoming.get(1)) {
        {
          org.apache.thrift.protocol.TList _list26 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
          struct.lstMotors = new java.util.ArrayList<CapMotorResult>(_list26.size);
          CapMotorResult _elem27;
          for (int _i28 = 0; _i28 < _list26.size; ++_i28)
          {
            _elem27 = new CapMotorResult();
            _elem27.read(iprot);
            struct.lstMotors.add(_elem27);
          }
        }
        struct.setLstMotorsIsSet(true);
      }
      if (incoming.get(2)) {
        {
          org.apache.thrift.protocol.TList _list29 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
          struct.lstNonmotors = new java.util.ArrayList<CapNonmotorResult>(_list29.size);
          CapNonmotorResult _elem30;
          for (int _i31 = 0; _i31 < _list29.size; ++_i31)
          {
            _elem30 = new CapNonmotorResult();
            _elem30.read(iprot);
            struct.lstNonmotors.add(_elem30);
          }
        }
        struct.setLstNonmotorsIsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

