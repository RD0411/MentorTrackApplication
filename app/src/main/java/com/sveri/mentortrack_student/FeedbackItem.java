package com.sveri.mentortrack_student;

public class FeedbackItem {
    private String topic;
    private String description;
    private String teacherName;

    public FeedbackItem(String topic, String description, String teacherName) {
        this.topic = topic;
        this.description = description;
        this.teacherName = teacherName;
    }

    public String getTopic() {
        return topic;
    }

    public String getDescription() {
        return description;
    }

    public String getTeacherName() {
        return teacherName;
    }
}
