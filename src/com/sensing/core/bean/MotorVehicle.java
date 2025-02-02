package com.sensing.core.bean;

import java.io.Serializable;
import java.util.Date;

import com.sensing.core.thrift.cap.bean.CapMotorResult;
import com.sensing.core.utils.StringUtils;
import com.sensing.core.utils.MatchUtil;
import com.sensing.core.utils.time.DateStyle;
import com.sensing.core.utils.time.TransfTimeUtil;

/**
 * @author wenbo
 */
public class MotorVehicle implements Serializable {
	// private static final long serialVersionUID = 1L;
	private String uuid;// uuid
	private Integer capFlag;//抓拍标识;1:实时结构化任务；2：历史任务；3：离线任务
	private Integer type;// 使用类型
	private String deviceId;// 设备id
	private String deviceName;// 设备名称
	private Long capTime;// 抓拍时间
	private Date capDate;
	private String capTimeStr;
	private Integer frameTime;// 视频帧数
	private String frameTimeStr;
	private String plateNo;// 车牌号码
	private Integer vehicleColor;// 车辆颜色
	private String vehicleColorTag;
	private Integer orientation;// 朝向
	private String orientationTag;
	private Integer plateColor;// 车牌颜色
	private String plateColorTag;
	private Integer vehicleClass;// 车辆类型
	private String vehicleClassTag;
	private Integer plateClass; // 车牌种类
	private String plateClassTag;
	private Integer vehicleBrand;// 机动车主品牌
	private String vehicleBrandTag;
	private Integer vehicleModel;// 机动车子品牌
	private String vehicleModelTag;
	private Integer vehicleStyles;// 机动车年款
	private String vehicleStylesTag;
	private Integer vehicleMarkerMot;// 年检标
	private String vehicleMarkerMotTag;
	private Integer vehicleMarkerTissuebox;// 纸巾盒
	private String vehicleMarkerTissueboxTag;
	private Integer vehicleMarkerPendant;// 挂饰
	private String vehicleMarkerPendantTag;
	private Integer sunvisor;// 遮阳板
	private String sunvisorTag;
	private Integer safetyBelt;// 主驾驶系安全带
	private String safetyBeltTag;
	private Integer safetyBeltCopilot;// 副驾驶系安全带
	private String safetyBeltCopilotTag;
	private Integer calling;// 主驾驶是否打手机
	private String callingTag;
	private String capUrl;// 抓拍图地址
	private String seceneUrl;// 场景图地址
	private String videoUrl;// 视频文件播放地址
	private Integer isDeleted;// 删除标记
	private String createUser;//
	private java.util.Date createTime;
	private String createTimeStr;
	private String modifyUser;
	private java.util.Date modifyTime;
	private String modifyTimeStr;
	private String capLocation;// 抓拍人在场景图中的坐标
	private Integer capType;// 抓拍类型
	private String capFeature;// 特征文件
	private Float score;// 比对分值
	private Integer sceneWidth;
	private Integer sceneHeight;
	private String channelName;
	private String channelArea;
	private String imgData;
	private byte[] fea;//特征文件
	
	public MotorVehicle() {

	}

	public MotorVehicle(CapMotorResult capMotorResult) {
		this.plateNo = capMotorResult.getPlateLicence();
		this.vehicleColor = capMotorResult.getCarColor();
		this.orientation = capMotorResult.getOrientation();
		this.plateColor = capMotorResult.getPlateColor();
		this.vehicleClass = capMotorResult.getCarType();
		this.plateClass = capMotorResult.getBrandType();
		this.vehicleBrandTag = capMotorResult.getBrandMainTag();
		this.vehicleModelTag = capMotorResult.getBrandSubTag();
		this.vehicleStylesTag = capMotorResult.getBrandYearTag();
		this.vehicleMarkerMot = capMotorResult.getCarMarkerMot();
		this.vehicleMarkerTissuebox = capMotorResult.getCarMarkerTissuebox();
		this.vehicleMarkerPendant = capMotorResult.getCarMarkerPendant();
		this.sunvisor = capMotorResult.getCarMarkerSunvisor();
		this.safetyBelt = capMotorResult.getSeatbeltDriver();
		this.safetyBeltCopilot = capMotorResult.getSeatbeltCopilot();
		this.calling = capMotorResult.getPhonecallDriver();
		this.capLocation = capMotorResult.getCapLocation();
		this.capType = capMotorResult.getCapType();
		if (capMotorResult.getCapFeature() != null && capMotorResult.getCapFeature().length > 0) {
			this.capFeature = StringUtils.byte2Base64StringFun(capMotorResult.getCapFeature());
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

	public String getPlateNo() {
		return plateNo;
	}

	public void setPlateNo(String plateNo) {
		this.plateNo = plateNo;
	}

	public Integer getVehicleColor() {
		return vehicleColor;
	}

	public void setVehicleColor(Integer vehicleColor) {
		this.vehicleColor = vehicleColor;
	}

	public Integer getOrientation() {
		return orientation;
	}

	public void setOrientation(Integer orientation) {
		this.orientation = orientation;
	}

	public Integer getPlateColor() {
		return plateColor;
	}

	public void setPlateColor(Integer plateColor) {
		this.plateColor = plateColor;
	}

	public Integer getVehicleClass() {
		return vehicleClass;
	}

	public void setVehicleClass(Integer vehicleClass) {
		this.vehicleClass = vehicleClass;
	}

	public Integer getPlateClass() {
		return plateClass;
	}

	public void setPlateClass(Integer plateClass) {
		this.plateClass = plateClass;
	}

	public Integer getVehicleBrand() {
		return vehicleBrand;
	}

	public void setVehicleBrand(Integer vehicleBrand) {
		this.vehicleBrand = vehicleBrand;
	}

	public Integer getVehicleModel() {
		return vehicleModel;
	}

	public void setVehicleModel(Integer vehicleModel) {
		this.vehicleModel = vehicleModel;
	}

	public Integer getVehicleStyles() {
		return vehicleStyles;
	}

	public void setVehicleStyles(Integer vehicleStyles) {
		this.vehicleStyles = vehicleStyles;
	}

	public Integer getVehicleMarkerMot() {
		return vehicleMarkerMot;
	}

	public void setVehicleMarkerMot(Integer vehicleMarkerMot) {
		this.vehicleMarkerMot = vehicleMarkerMot;
	}

	public Integer getVehicleMarkerTissuebox() {
		return vehicleMarkerTissuebox;
	}

	public void setVehicleMarkerTissuebox(Integer vehicleMarkerTissuebox) {
		this.vehicleMarkerTissuebox = vehicleMarkerTissuebox;
	}

	public Integer getVehicleMarkerPendant() {
		return vehicleMarkerPendant;
	}

	public void setVehicleMarkerPendant(Integer vehicleMarkerPendant) {
		this.vehicleMarkerPendant = vehicleMarkerPendant;
	}

	public Integer getSunvisor() {
		return sunvisor;
	}

	public void setSunvisor(Integer sunvisor) {
		this.sunvisor = sunvisor;
	}

	public Integer getSafetyBelt() {
		return safetyBelt;
	}

	public void setSafetyBelt(Integer safetyBelt) {
		this.safetyBelt = safetyBelt;
	}

	public Integer getSafetyBeltCopilot() {
		return safetyBeltCopilot;
	}

	public void setSafetyBeltCopilot(Integer safetyBeltCopilot) {
		this.safetyBeltCopilot = safetyBeltCopilot;
	}

	public Integer getCalling() {
		return calling;
	}

	public void setCalling(Integer calling) {
		this.calling = calling;
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

	public String getVehicleColorTag() {
		return vehicleColorTag;
	}

	public void setVehicleColorTag(String vehicleColorTag) {
		this.vehicleColorTag = vehicleColorTag;
	}

	public String getOrientationTag() {
		return orientationTag;
	}

	public void setOrientationTag(String orientationTag) {
		this.orientationTag = orientationTag;
	}

	public String getPlateColorTag() {
		return plateColorTag;
	}

	public void setPlateColorTag(String plateColorTag) {
		this.plateColorTag = plateColorTag;
	}

	public String getVehicleClassTag() {
		return vehicleClassTag;
	}

	public void setVehicleClassTag(String vehicleClassTag) {
		this.vehicleClassTag = vehicleClassTag;
	}

	public String getPlateClassTag() {
		return plateClassTag;
	}

	public void setPlateClassTag(String plateClassTag) {
		this.plateClassTag = plateClassTag;
	}

	public String getVehicleBrandTag() {
		return vehicleBrandTag;
	}

	public void setVehicleBrandTag(String vehicleBrandTag) {
		this.vehicleBrandTag = vehicleBrandTag;
	}

	public String getVehicleModelTag() {
		return vehicleModelTag;
	}

	public void setVehicleModelTag(String vehicleModelTag) {
		this.vehicleModelTag = vehicleModelTag;
	}

	public String getVehicleStylesTag() {
		return vehicleStylesTag;
	}

	public void setVehicleStylesTag(String vehicleStylesTag) {
		this.vehicleStylesTag = vehicleStylesTag;
	}

	public String getVehicleMarkerMotTag() {
		return vehicleMarkerMotTag;
	}

	public void setVehicleMarkerMotTag(String vehicleMarkerMotTag) {
		this.vehicleMarkerMotTag = vehicleMarkerMotTag;
	}

	public String getVehicleMarkerTissueboxTag() {
		return vehicleMarkerTissueboxTag;
	}

	public void setVehicleMarkerTissueboxTag(String vehicleMarkerTissueboxTag) {
		this.vehicleMarkerTissueboxTag = vehicleMarkerTissueboxTag;
	}

	public String getVehicleMarkerPendantTag() {
		return vehicleMarkerPendantTag;
	}

	public void setVehicleMarkerPendantTag(String vehicleMarkerPendantTag) {
		this.vehicleMarkerPendantTag = vehicleMarkerPendantTag;
	}

	public String getSunvisorTag() {
		return sunvisorTag;
	}

	public void setSunvisorTag(String sunvisorTag) {
		this.sunvisorTag = sunvisorTag;
	}

	public String getSafetyBeltTag() {
		return safetyBeltTag;
	}

	public void setSafetyBeltTag(String safetyBeltTag) {
		this.safetyBeltTag = safetyBeltTag;
	}

	public String getSafetyBeltCopilotTag() {
		return safetyBeltCopilotTag;
	}

	public void setSafetyBeltCopilotTag(String safetyBeltCopilotTag) {
		this.safetyBeltCopilotTag = safetyBeltCopilotTag;
	}

	public String getCallingTag() {
		return callingTag;
	}

	public void setCallingTag(String callingTag) {
		this.callingTag = callingTag;
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

	public byte[] getFea() {
		return fea;
	}

	public void setFea(byte[] fea) {
		this.fea = fea;
	}

	/**
	 * 告警判断对象是否相同
	 * 
	 * @param capMotor
	 * @return
	 * @author mingxingyu
	 * @date 2018年10月30日 上午10:07:31
	 */
	public boolean equalAlarm(MotorVehicle motorVehicle) {

//		if ( this == capMotor ){ return true; }
//		if ( capMotor == null ){ return false; }
//		if ( this.carColor != capMotor.getCarColor() ) { return false; } 
//		if ( this.orientation != capMotor.getOrientation() ) { return false; } 
//		if ( this.plateColor != capMotor.getPlateColor() ) { return false; } 
//		if ( this.carType != capMotor.getCarType() ) { return false; } 
//		if ( this.brandType != capMotor.getBrandType() ) { return false; } 
//		if ( this.brandMainTag != capMotor.getBrandMainTag() ) { return false; } 
//		if ( this.brandSubTag != capMotor.getBrandSubTag() ) { return false; } 
//		if ( this.brandYearTag != capMotor.getBrandYearTag() ) { return false; } 
//		if ( this.carMarkerMot != capMotor.getCarMarkerMot() ) { return false; } 
//		if ( this.carMarkerTissuebox != capMotor.getCarMarkerTissuebox() ) { return false; } 
//		if ( this.carMarkerPendant != capMotor.getCarMarkerPendant() ) { return false; } 
//		if ( this.carMarkerSunvisor != capMotor.getCarMarkerSunvisor() ) { return false; } 
//		if ( this.seatbeltDriver != capMotor.getSeatbeltDriver() ) { return false; } 
//		if ( this.seatbeltCopilot != capMotor.getSeatbeltCopilot() ) { return false; } 
//		if ( this.phonecallDriver != capMotor.getPhonecallDriver() ) { return false; } 

		return true;
	}
}