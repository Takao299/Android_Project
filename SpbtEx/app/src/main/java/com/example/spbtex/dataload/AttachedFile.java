package com.example.spbtex.dataload;

public class AttachedFile {

    private Integer afId;
    private Integer foreignId;
    private String fileName;
    private String createTime;
    private String delete_pic;
    private String deleteDateTime;


    public Integer getAfId() {
        return afId;
    }

    public void setAfId(Integer afId) {
        this.afId = afId;
    }

    public Integer getForeignId() {
        return foreignId;
    }

    public void setForeignId(Integer foreignId) {
        this.foreignId = foreignId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDelete_pic() {
        return delete_pic;
    }

    public void setDelete_pic(String delete_pic) {
        this.delete_pic = delete_pic;
    }

    public String getDeleteDateTime() {
        return deleteDateTime;
    }

    public void setDeleteDateTime(String deleteDateTime) {
        this.deleteDateTime = deleteDateTime;
    }

    public AttachedFile() {
    }

    public AttachedFile(Integer afId, Integer foreignId, String fileName, String createTime, String delete_pic, String deleteDateTime) {
        this.afId = afId;
        this.foreignId = foreignId;
        this.fileName = fileName;
        this.createTime = createTime;
        this.delete_pic = delete_pic;
        this.deleteDateTime = deleteDateTime;
    }
}
