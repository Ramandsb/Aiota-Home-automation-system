package in.tagplug.tagplug;

/**
 * Created by admin pc on 05-05-2016.
 */
public class DataItems {
    private String title,image,app_id,start_hour,start_min,end_hour,end_min,unique_stamp;


    public DataItems() {
    }

    public DataItems(String title,String imagename,String app_id){
                     this.title=title;
        this.image=imagename;
        this.app_id=app_id;

    }

    public String getTitle() {
        return  title;
    }

    public void setTitle(String title) {

        this.title= title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    public String getAppid() {
        return app_id;
    }

    public void setAppid(String app_id) {
        this.app_id = app_id;
    }
    public String getStart_hour() {
        return start_hour;
    }

    public void setStart_hour(String start_hour) {
        this.start_hour = start_hour;
    }
    public String getStart_min() {
        return start_min;
    }

    public void setStart_min(String start_min) {
        this.start_min = start_min;
    }
    public String getEnd_hour() {
        return end_hour;
    }

    public void setEnd_hour(String end_hour) {
        this.end_hour = end_hour;
    }
    public String getEnd_min() {
        return end_min;
    }

    public void setEnd_min(String end_min) {
        this.end_min = end_min;
    }
    public String getUnique_stamp() {
        return unique_stamp;
    }

    public void setUnique_stamp(String unique_stamp) {
        this.unique_stamp = unique_stamp;
    }
}
