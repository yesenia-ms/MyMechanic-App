package cs3773.group11.mymechanic;

public class CommentItem {

    String name;
    String role;
    String comment;

    public CommentItem(String name, String role, String comment) {
        this.name = name;
        this.role = role;
        this.comment = comment;
    }

    public CommentItem() {
        this.name = "name";
        this.role = "role";
        this.comment = "comment";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
