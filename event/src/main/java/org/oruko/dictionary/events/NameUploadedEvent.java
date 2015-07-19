package org.oruko.dictionary.events;

/**
 * Event object for when a name is uploaded
 * Created by Dadepo Aderemi.
 */
public class NameUploadedEvent {
    private int totalNumberOfNames;
    private int totalUploaded;
    private boolean isUploading = false;

    public boolean isUploading() {
        return isUploading;
    }

    public void isUploading(boolean isUploading) {
        this.isUploading = isUploading;
    }

    public void setTotalNumberOfNames(int totalNumberOfNames) {
        this.totalNumberOfNames = totalNumberOfNames;
    }

    public void setTotalUploaded(int totalUploaded) {
        this.totalUploaded = totalUploaded;
    }

    public int getTotalNumberOfNames() {
        return totalNumberOfNames;
    }

    public int getTotalUploaded() {
        return totalUploaded;
    }
}
