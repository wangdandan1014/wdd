package com.sensing.core.thrift.bean;
/**
 * Autogenerated by Thrift Compiler (0.10.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.10.0)", date = "2018-08-01")
public class CompareFeatureDBList implements org.apache.thrift.TBase<CompareFeatureDBList, CompareFeatureDBList._Fields>, java.io.Serializable, Cloneable, Comparable<CompareFeatureDBList> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("CompareFeatureDBList");

  private static final org.apache.thrift.protocol.TField INDEX_FIELD_DESC = new org.apache.thrift.protocol.TField("index", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField SCORE_FIELD_DESC = new org.apache.thrift.protocol.TField("score", org.apache.thrift.protocol.TType.DOUBLE, (short)2);
  private static final org.apache.thrift.protocol.TField FT_DB_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("ftDbId", org.apache.thrift.protocol.TType.I32, (short)3);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new CompareFeatureDBListStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new CompareFeatureDBListTupleSchemeFactory();

  public int index; // required
  public double score; // required
  public int ftDbId; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    INDEX((short)1, "index"),
    SCORE((short)2, "score"),
    FT_DB_ID((short)3, "ftDbId");

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
        case 1: // INDEX
          return INDEX;
        case 2: // SCORE
          return SCORE;
        case 3: // FT_DB_ID
          return FT_DB_ID;
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
  private static final int __INDEX_ISSET_ID = 0;
  private static final int __SCORE_ISSET_ID = 1;
  private static final int __FTDBID_ISSET_ID = 2;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.INDEX, new org.apache.thrift.meta_data.FieldMetaData("index", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.SCORE, new org.apache.thrift.meta_data.FieldMetaData("score", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.DOUBLE)));
    tmpMap.put(_Fields.FT_DB_ID, new org.apache.thrift.meta_data.FieldMetaData("ftDbId", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(CompareFeatureDBList.class, metaDataMap);
  }

  public CompareFeatureDBList() {
  }

  public CompareFeatureDBList(
    int index,
    double score,
    int ftDbId)
  {
    this();
    this.index = index;
    setIndexIsSet(true);
    this.score = score;
    setScoreIsSet(true);
    this.ftDbId = ftDbId;
    setFtDbIdIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public CompareFeatureDBList(CompareFeatureDBList other) {
    __isset_bitfield = other.__isset_bitfield;
    this.index = other.index;
    this.score = other.score;
    this.ftDbId = other.ftDbId;
  }

  public CompareFeatureDBList deepCopy() {
    return new CompareFeatureDBList(this);
  }

  @Override
  public void clear() {
    setIndexIsSet(false);
    this.index = 0;
    setScoreIsSet(false);
    this.score = 0.0;
    setFtDbIdIsSet(false);
    this.ftDbId = 0;
  }

  public int getIndex() {
    return this.index;
  }

  public CompareFeatureDBList setIndex(int index) {
    this.index = index;
    setIndexIsSet(true);
    return this;
  }

  public void unsetIndex() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __INDEX_ISSET_ID);
  }

  /** Returns true if field index is set (has been assigned a value) and false otherwise */
  public boolean isSetIndex() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __INDEX_ISSET_ID);
  }

  public void setIndexIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __INDEX_ISSET_ID, value);
  }

  public double getScore() {
    return this.score;
  }

  public CompareFeatureDBList setScore(double score) {
    this.score = score;
    setScoreIsSet(true);
    return this;
  }

  public void unsetScore() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __SCORE_ISSET_ID);
  }

  /** Returns true if field score is set (has been assigned a value) and false otherwise */
  public boolean isSetScore() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __SCORE_ISSET_ID);
  }

  public void setScoreIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __SCORE_ISSET_ID, value);
  }

  public int getFtDbId() {
    return this.ftDbId;
  }

  public CompareFeatureDBList setFtDbId(int ftDbId) {
    this.ftDbId = ftDbId;
    setFtDbIdIsSet(true);
    return this;
  }

  public void unsetFtDbId() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __FTDBID_ISSET_ID);
  }

  /** Returns true if field ftDbId is set (has been assigned a value) and false otherwise */
  public boolean isSetFtDbId() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __FTDBID_ISSET_ID);
  }

  public void setFtDbIdIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __FTDBID_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, java.lang.Object value) {
    switch (field) {
    case INDEX:
      if (value == null) {
        unsetIndex();
      } else {
        setIndex((java.lang.Integer)value);
      }
      break;

    case SCORE:
      if (value == null) {
        unsetScore();
      } else {
        setScore((java.lang.Double)value);
      }
      break;

    case FT_DB_ID:
      if (value == null) {
        unsetFtDbId();
      } else {
        setFtDbId((java.lang.Integer)value);
      }
      break;

    }
  }

  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case INDEX:
      return getIndex();

    case SCORE:
      return getScore();

    case FT_DB_ID:
      return getFtDbId();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case INDEX:
      return isSetIndex();
    case SCORE:
      return isSetScore();
    case FT_DB_ID:
      return isSetFtDbId();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof CompareFeatureDBList)
      return this.equals((CompareFeatureDBList)that);
    return false;
  }

  public boolean equals(CompareFeatureDBList that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_index = true;
    boolean that_present_index = true;
    if (this_present_index || that_present_index) {
      if (!(this_present_index && that_present_index))
        return false;
      if (this.index != that.index)
        return false;
    }

    boolean this_present_score = true;
    boolean that_present_score = true;
    if (this_present_score || that_present_score) {
      if (!(this_present_score && that_present_score))
        return false;
      if (this.score != that.score)
        return false;
    }

    boolean this_present_ftDbId = true;
    boolean that_present_ftDbId = true;
    if (this_present_ftDbId || that_present_ftDbId) {
      if (!(this_present_ftDbId && that_present_ftDbId))
        return false;
      if (this.ftDbId != that.ftDbId)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + index;

    hashCode = hashCode * 8191 + org.apache.thrift.TBaseHelper.hashCode(score);

    hashCode = hashCode * 8191 + ftDbId;

    return hashCode;
  }

  @Override
  public int compareTo(CompareFeatureDBList other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSetIndex()).compareTo(other.isSetIndex());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetIndex()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.index, other.index);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetScore()).compareTo(other.isSetScore());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetScore()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.score, other.score);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetFtDbId()).compareTo(other.isSetFtDbId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetFtDbId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.ftDbId, other.ftDbId);
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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("CompareFeatureDBList(");
    boolean first = true;

    sb.append("index:");
    sb.append(this.index);
    first = false;
    if (!first) sb.append(", ");
    sb.append("score:");
    sb.append(this.score);
    first = false;
    if (!first) sb.append(", ");
    sb.append("ftDbId:");
    sb.append(this.ftDbId);
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

  private static class CompareFeatureDBListStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public CompareFeatureDBListStandardScheme getScheme() {
      return new CompareFeatureDBListStandardScheme();
    }
  }

  private static class CompareFeatureDBListStandardScheme extends org.apache.thrift.scheme.StandardScheme<CompareFeatureDBList> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, CompareFeatureDBList struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // INDEX
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.index = iprot.readI32();
              struct.setIndexIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // SCORE
            if (schemeField.type == org.apache.thrift.protocol.TType.DOUBLE) {
              struct.score = iprot.readDouble();
              struct.setScoreIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // FT_DB_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.ftDbId = iprot.readI32();
              struct.setFtDbIdIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, CompareFeatureDBList struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(INDEX_FIELD_DESC);
      oprot.writeI32(struct.index);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(SCORE_FIELD_DESC);
      oprot.writeDouble(struct.score);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(FT_DB_ID_FIELD_DESC);
      oprot.writeI32(struct.ftDbId);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class CompareFeatureDBListTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public CompareFeatureDBListTupleScheme getScheme() {
      return new CompareFeatureDBListTupleScheme();
    }
  }

  private static class CompareFeatureDBListTupleScheme extends org.apache.thrift.scheme.TupleScheme<CompareFeatureDBList> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, CompareFeatureDBList struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSetIndex()) {
        optionals.set(0);
      }
      if (struct.isSetScore()) {
        optionals.set(1);
      }
      if (struct.isSetFtDbId()) {
        optionals.set(2);
      }
      oprot.writeBitSet(optionals, 3);
      if (struct.isSetIndex()) {
        oprot.writeI32(struct.index);
      }
      if (struct.isSetScore()) {
        oprot.writeDouble(struct.score);
      }
      if (struct.isSetFtDbId()) {
        oprot.writeI32(struct.ftDbId);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, CompareFeatureDBList struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet incoming = iprot.readBitSet(3);
      if (incoming.get(0)) {
        struct.index = iprot.readI32();
        struct.setIndexIsSet(true);
      }
      if (incoming.get(1)) {
        struct.score = iprot.readDouble();
        struct.setScoreIsSet(true);
      }
      if (incoming.get(2)) {
        struct.ftDbId = iprot.readI32();
        struct.setFtDbIdIsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

