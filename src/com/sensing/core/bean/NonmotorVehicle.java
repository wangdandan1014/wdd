package com.sensing.core.bean;

import java.io.Serializable;
import java.util.Date;

import com.sensing.core.thrift.cap.bean.CapNonmotorResult;
import com.sensing.core.utils.StringUtils;
import com.sensing.core.utils.MatchUtil;
import com.sensing.core.utils.time.DateStyle;
import com.sensing.core.utils.time.TransfTimeUtil;

/**
 * @author wenbo
 */
public class NonmotorVehicle implements Serializable {
//	private static final long serialVersionUID = 1L;
	private String uuid;
	private Integer capFlag;//抓拍标识;1:实时结构化任务；2：历史任务；3：离线任务
	private Integer type;
	private String deviceId;
	private String deviceName;
	private Long capTime;
	private Date capDate;
	private String capTimeStr;
	private Integer frameTime;
	private String frameTimeStr;
	private Integer orientation;
	private String orientationTag;
//	private Integer carColor;
	private Integer vehicleColor;
	private String vehicleColorTag;
//	private Integer carType;
//	private Integer vehicleType;
//	private String vehicleTypeTag;
	private Integer vehicleClass;
	private String vehicleClassTag;
//	private Integer moveState;
	private Integer motion;
	private String motionTag;
//	private Integer sex;
	private Integer genderCode;
	private String genderCodeTag;
	private Integer age;
	private String ageTag;
//	private Integer hat;
	private Integer cap;
	private String capTag;
	private Integer glass;
	private String glassTag;
//	private Integer mask;
	private Integer respirator;
	private String respiratorTag;
//	private Integer upperClothesColor;
	private Integer coatColor;
	private String coatColorTag;
//	private Integer upperClothesType;
	private Integer coatLength;
	private String coatLengthTag;
//	private Integer upperClothesTexture;
	private Integer coatTexture;
	private String coatTextureTag;
	private String capUrl;
	private String seceneUrl;
	private String videoUrl;
	private Integer isDeleted;
	private String createUser;
	private java.util.Date createTime;
	private String createTimeStr;
	private String modifyUser;
	private java.util.Date modifyTime;
	private String modifyTimeStr;
	private String capLocation;
	private Integer capType;
	private String capFeature;
	private Float score;
	private Integer sceneWidth;
	private Integer sceneHeight;
	private String channelName;
	private String channelArea;
	private String imgData;

	public NonmotorVehicle() {

	}

	public NonmotorVehicle(CapNonmotorResult capNonmotorResult) {
		this.orientation = capNonmotorResult.getOrientation();
		this.vehicleColor = capNonmotorResult.getCarColor();
		this.vehicleClass = capNonmotorResult.getCarType();
		this.motion = capNonmotorResult.getMoveState();
		this.genderCode = capNonmotorResult.getSex();
		this.age = capNonmotorResult.getAge();
		this.cap = capNonmotorResult.getHat();
		this.glass = capNonmotorResult.getGlass();
		this.respirator = capNonmotorResult.getMask();
		this.coatColor = capNonmotorResult.getUpperClothesColor();
		this.coatLength = capNonmotorResult.getUpperClothesType();
		this.capLocation = capNonmotorResult.getCapLocation();
		this.capType = capNonmotorResult.getCapType();
		if (capNonmotorResult.getCapFeature() != null && capNonmotorResult.getCapFeature().length > 0) {
			this.capFeature = StringUtils.byte2Base64StringFun(capNonmotorResult.getCapFeature());
		}
	}

	public Integer getCapFlag() {
		return capFlag;
	}

	public void setCapFlag(Integer capFlag) {
		this.capFlag = capFlag;
	}

	public Date getCapDate() {
		return capDate;
	}

	public void setCapDate(Date capDate) {
		this.capDate = capDate;
	}

	public String getImgData() {
		return imgData;
	}

	public void setImgData(String imgData) {
		this.imgData = imgData;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getChannelArea() {
		return channelArea;
	}

	public void setChannelArea(String channelArea) {
		this.channelArea = channelArea;
	}

	public Integer getSceneWidth() {
		return sceneWidth;
	}

	public void setSceneWidth(Integer sceneWidth) {
		this.sceneWidth = sceneWidth;
	}

	public Integer getSceneHeight() {
		return sceneHeight;
	}

	public void setSceneHeight(Integer sceneHeight) {
		this.sceneHeight = sceneHeight;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public Float getScore() {
		return score;
	}

	public void setScore(Float score) {
		this.score = score;
	}

	public String getCapFeature() {
		return capFeature;
	}

	public void setCapFeature(String capFeature) {
		this.capFeature = capFeature;
	}

	public String getSeceneUrl() {
		return seceneUrl;
	}

	public void setSeceneUrl(String seceneUrl) {
		this.seceneUrl = seceneUrl;
	}

	public Integer getCapType() {
		return capType;
	}

	public void setCapType(Integer capType) {
		this.capType = capType;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public Long getCapTime() {
		return capTime;
	}

	public void setCapTime(Long capTime) {
		this.capTime = capTime;
	}

	public Integer getOrientation() {
		return orientation;
	}

	public void setOrientation(Integer orientation) {
		this.orientation = orientation;
	}

	public Integer getVehicleColor() {
		return vehicleColor;
	}

	public void setVehicleColor(Integer vehicleColor) {
		this.vehicleColor = vehicleColor;
	}

	public Integer getVehicleClass() {
		return vehicleClass;
	}

	public void setVehicleClass(Integer vehicleClass) {
		this.vehicleClass = vehicleClass;
	}

	public Integer getMotion() {
		return motion;
	}

	public void setMotion(Integer motion) {
		this.motion = motion;
	}

	public Integer getGenderCode() {
		return genderCode;
	}

	public void setGenderCode(Integer genderCode) {
		this.genderCode = genderCode;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Integer getCap() {
		return cap;
	}

	public void setCap(Integer cap) {
		this.cap = cap;
	}

	public Integer getGlass() {
		return glass;
	}

	public void setGlass(Integer glass) {
		this.glass = glass;
	}

	public Integer getRespirator() {
		return respirator;
	}

	public void setRespirator(Integer respirator) {
		this.respirator = respirator;
	}

	public Integer getCoatColor() {
		return coatColor;
	}

	public void setCoatColor(Integer coatColor) {
		this.coatColor = coatColor;
	}

	public Integer getCoatLength() {
		return coatLength;
	}

	public void setCoatLength(Integer coatLength) {
		this.coatLength = coatLength;
	}

	public Integer getCoatTexture() {
		return coatTexture;
	}

	public void setCoatTexture(Integer coatTexture) {
		this.coatTexture = coatTexture;
	}

	public String getCapUrl() {
		return capUrl;
	}

	public void setCapUrl(String capUrl) {
		this.capUrl = capUrl;
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	public Integer getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Integer isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public java.util.Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(java.util.Date createTime) {
		this.createTime = createTime;
	}

	public String getCreateTimeStr() {
		if (createTime != null) {
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
			return sdf.format(createTime);
		} else {
			return "";
		}
	}

	public void setCreateTimeStr(String createTimeStr) throws Exception {
		if (createTimeStr != null && !createTimeStr.trim().equals("")) {
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
			this.createTime = sdf.parse(createTimeStr);
		} else
			this.createTime = null;
	}

	public String getModifyUser() {
		return modifyUser;
	}

	public void setModifyUser(String modifyUser) {
		this.modifyUser = modifyUser;
	}

	public java.util.Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(java.util.Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	public String getModifyTimeStr() {
		if (modifyTime != null) {
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
			return sdf.format(modifyTime);
		} else {
			return "";
		}
	}

	public void setModifyTimeStr(String modifyTimeStr) throws Exception {
		if (modifyTimeStr != null && !modifyTimeStr.trim().equals("")) {
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
			this.modifyTime = sdf.parse(modifyTimeStr);
		} else
			this.modifyTime = null;
	}

	public String getOrientationTag() {
		return orientationTag;
	}

	public void setOrientationTag(String orientationTag) {
		this.orientationTag = orientationTag;
	}

	public String getVehicleColorTag() {
		return vehicleColorTag;
	}

	public void setVehicleColorTag(String vehicleColorTag) {
		this.vehicleColorTag = vehicleColorTag;
	}

	public String getVehicleClassTag() {
		return vehicleClassTag;
	}

	public void setVehicleClassTag(String vehicleClassTag) {
		this.vehicleClassTag = vehicleClassTag;
	}

	public String getMotionTag() {
		return motionTag;
	}

	public void setMotionTag(String motionTag) {
		this.motionTag = motionTag;
	}

	public String getGenderCodeTag() {
		return genderCodeTag;
	}

	public void setGenderCodeTag(String genderCodeTag) {
		this.genderCodeTag = genderCodeTag;
	}

	public String getAgeTag() {
		return ageTag;
	}

	public void setAgeTag(String ageTag) {
		this.ageTag = ageTag;
	}

	public String getCapTag() {
		return capTag;
	}

	public void setCapTag(String capTag) {
		this.capTag = capTag;
	}

	public String getGlassTag() {
		return glassTag;
	}

	public void setGlassTag(String glassTag) {
		this.glassTag = glassTag;
	}

	public String getRespiratorTag() {
		return respiratorTag;
	}

	public void setRespiratorTag(String respiratorTag) {
		this.respiratorTag = respiratorTag;
	}

	public String getCoatColorTag() {
		return coatColorTag;
	}

	public void setCoatColorTag(String coatColorTag) {
		this.coatColorTag = coatColorTag;
	}

	public String getCoatLengthTag() {
		return coatLengthTag;
	}

	public void setCoatLengthTag(String coatLengthTag) {
		this.coatLengthTag = coatLengthTag;
	}

	public String getCoatTextureTag() {
		return coatTextureTag;
	}

	public void setCoatTextureTag(String coatTextureTag) {
		this.coatTextureTag = coatTextureTag;
	}

	public String getCapTimeStr() {
		if (capTime != null && MatchUtil.isTenPositive(capTime.toString())) {
			String formatCapTime = TransfTimeUtil.UnixTimeStamp2Date(capTime, DateStyle.YYYY_MM_DD_HH_MM_SS);
			return formatCapTime;
		} else {
			return "";
		}
	}

	public void setCapTimeStr(String capTimeStr) {
		this.capTimeStr = capTimeStr;
	}

	public Integer getFrameTime() {
		return frameTime;
	}

	public void setFrameTime(Integer frameTime) {
		this.frameTime = frameTime;
	}

	public String getFrameTimeStr() {
		if (frameTime != null && frameTime < 360000) {
			String frameTimeStr = TransfTimeUtil.getHMS(frameTime);
			return frameTimeStr;
		} else {
			return "";
		}
	}

	public void setFrameTimeStr(String frameTimeStr) {
		this.frameTimeStr = frameTimeStr;
	}

	public String getCapLocation() {
		return capLocation;
	}

	public void setCapLocation(String capLocation) {
		this.capLocation = capLocation;
	}

}