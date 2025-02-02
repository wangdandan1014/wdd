package com.sensing.core.thrift.cap.bean;
/**
 * Autogenerated by Thrift Compiler (0.10.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.10.0)", date = "2019-01-02")
public class CapSceneImageResult implements org.apache.thrift.TBase<CapSceneImageResult, CapSceneImageResult._Fields>, java.io.Serializable, Cloneable, Comparable<CapSceneImageResult> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("CapSceneImageResult");

  private static final org.apache.thrift.protocol.TField I_RET_FIELD_DESC = new org.apache.thrift.protocol.TField("iRet", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField BIN_SCENE_IMG_FIELD_DESC = new org.apache.thrift.protocol.TField("binSceneImg", org.apache.thrift.protocol.TType.STRING, (short)2);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new CapSceneImageResultStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new CapSceneImageResultTupleSchemeFactory();

  public int iRet; // required
  public java.nio.ByteBuffer binSceneImg; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    I_RET((short)1, "iRet"),
    BIN_SCENE_IMG((short)2, "binSceneImg");

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
        case 1: // I_RET
          return I_RET;
        case 2: // BIN_SCENE_IMG
          return BIN_SCENE_IMG;
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
  private static final int __IRET_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.I_RET, new org.apache.thrift.meta_data.FieldMetaData("iRet", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.BIN_SCENE_IMG, new org.apache.thrift.meta_data.FieldMetaData("binSceneImg", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING        , true)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(CapSceneImageResult.class, metaDataMap);
  }

  public CapSceneImageResult() {
  }

  public CapSceneImageResult(
    int iRet,
    java.nio.ByteBuffer binSceneImg)
  {
    this();
    this.iRet = iRet;
    setIRetIsSet(true);
    this.binSceneImg = org.apache.thrift.TBaseHelper.copyBinary(binSceneImg);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public CapSceneImageResult(CapSceneImageResult other) {
    __isset_bitfield = other.__isset_bitfield;
    this.iRet = other.iRet;
    if (other.isSetBinSceneImg()) {
      this.binSceneImg = org.apache.thrift.TBaseHelper.copyBinary(other.binSceneImg);
    }
  }

  public CapSceneImageResult deepCopy() {
    return new CapSceneImageResult(this);
  }

  @Override
  public void clear() {
    setIRetIsSet(false);
    this.iRet = 0;
    this.binSceneImg = null;
  }

  public int getIRet() {
    return this.iRet;
  }

  public CapSceneImageResult setIRet(int iRet) {
    this.iRet = iRet;
    setIRetIsSet(true);
    return this;
  }

  public void unsetIRet() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __IRET_ISSET_ID);
  }

  /** Returns true if field iRet is set (has been assigned a value) and false otherwise */
  public boolean isSetIRet() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __IRET_ISSET_ID);
  }

  public void setIRetIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __IRET_ISSET_ID, value);
  }

  public byte[] getBinSceneImg() {
    setBinSceneImg(org.apache.thrift.TBaseHelper.rightSize(binSceneImg));
    return binSceneImg == null ? null : binSceneImg.array();
  }

  public java.nio.ByteBuffer bufferForBinSceneImg() {
    return org.apache.thrift.TBaseHelper.copyBinary(binSceneImg);
  }

  public CapSceneImageResult setBinSceneImg(byte[] binSceneImg) {
    this.binSceneImg = binSceneImg == null ? (java.nio.ByteBuffer)null : java.nio.ByteBuffer.wrap(binSceneImg.clone());
    return this;
  }

  public CapSceneImageResult setBinSceneImg(java.nio.ByteBuffer binSceneImg) {
    this.binSceneImg = org.apache.thrift.TBaseHelper.copyBinary(binSceneImg);
    return this;
  }

  public void unsetBinSceneImg() {
    this.binSceneImg = null;
  }

  /** Returns true if field binSceneImg is set (has been assigned a value) and false otherwise */
  public boolean isSetBinSceneImg() {
    return this.binSceneImg != null;
  }

  public void setBinSceneImgIsSet(boolean value) {
    if (!value) {
      this.binSceneImg = null;
    }
  }

  public void setFieldValue(_Fields field, java.lang.Object value) {
    switch (field) {
    case I_RET:
      if (value == null) {
        unsetIRet();
      } else {
        setIRet((java.lang.Integer)value);
      }
      break;

    case BIN_SCENE_IMG:
      if (value == null) {
        unsetBinSceneImg();
      } else {
        if (value instanceof byte[]) {
          setBinSceneImg((byte[])value);
        } else {
          setBinSceneImg((java.nio.ByteBuffer)value);
        }
      }
      break;

    }
  }

  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case I_RET:
      return getIRet();

    case BIN_SCENE_IMG:
      return getBinSceneImg();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case I_RET:
      return isSetIRet();
    case BIN_SCENE_IMG:
      return isSetBinSceneImg();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof CapSceneImageResult)
      return this.equals((CapSceneImageResult)that);
    return false;
  }

  public boolean equals(CapSceneImageResult that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_iRet = true;
    boolean that_present_iRet = true;
    if (this_present_iRet || that_present_iRet) {
      if (!(this_present_iRet && that_present_iRet))
        return false;
      if (this.iRet != that.iRet)
        return false;
    }

    boolean this_present_binSceneImg = true && this.isSetBinSceneImg();
    boolean that_present_binSceneImg = true && that.isSetBinSceneImg();
    if (this_present_binSceneImg || that_present_binSceneImg) {
      if (!(this_present_binSceneImg && that_present_binSceneImg))
        return false;
      if (!this.binSceneImg.equals(that.binSceneImg))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + iRet;

    hashCode = hashCode * 8191 + ((isSetBinSceneImg()) ? 131071 : 524287);
    if (isSetBinSceneImg())
      hashCode = hashCode * 8191 + binSceneImg.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(CapSceneImageResult other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSetIRet()).compareTo(other.isSetIRet());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetIRet()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.iRet, other.iRet);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetBinSceneImg()).compareTo(other.isSetBinSceneImg());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetBinSceneImg()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.binSceneImg, other.binSceneImg);
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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("CapSceneImageResult(");
    boolean first = true;

    sb.append("iRet:");
    sb.append(this.iRet);
    first = false;
    if (!first) sb.append(", ");
    sb.append("binSceneImg:");
    if (this.binSceneImg == null) {
      sb.append("null");
    } else {
      org.apache.thrift.TBaseHelper.toString(this.binSceneImg, sb);
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
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class CapSceneImageResultStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public CapSceneImageResultStandardScheme getScheme() {
      return new CapSceneImageResultStandardScheme();
    }
  }

  private static class CapSceneImageResultStandardScheme extends org.apache.thrift.scheme.StandardScheme<CapSceneImageResult> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, CapSceneImageResult struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // I_RET
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.iRet = iprot.readI32();
              struct.setIRetIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // BIN_SCENE_IMG
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.binSceneImg = iprot.readBinary();
              struct.setBinSceneImgIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, CapSceneImageResult struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(I_RET_FIELD_DESC);
      oprot.writeI32(struct.iRet);
      oprot.writeFieldEnd();
      if (struct.binSceneImg != null) {
        oprot.writeFieldBegin(BIN_SCENE_IMG_FIELD_DESC);
        oprot.writeBinary(struct.binSceneImg);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class CapSceneImageResultTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public CapSceneImageResultTupleScheme getScheme() {
      return new CapSceneImageResultTupleScheme();
    }
  }

  private static class CapSceneImageResultTupleScheme extends org.apache.thrift.scheme.TupleScheme<CapSceneImageResult> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, CapSceneImageResult struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSetIRet()) {
        optionals.set(0);
      }
      if (struct.isSetBinSceneImg()) {
        optionals.set(1);
      }
      oprot.writeBitSet(optionals, 2);
      if (struct.isSetIRet()) {
        oprot.writeI32(struct.iRet);
      }
      if (struct.isSetBinSceneImg()) {
        oprot.writeBinary(struct.binSceneImg);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, CapSceneImageResult struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet incoming = iprot.readBitSet(2);
      if (incoming.get(0)) {
        struct.iRet = iprot.readI32();
        struct.setIRetIsSet(true);
      }
      if (incoming.get(1)) {
        struct.binSceneImg = iprot.readBinary();
        struct.setBinSceneImgIsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

