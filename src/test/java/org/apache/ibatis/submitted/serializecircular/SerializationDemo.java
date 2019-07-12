import java.io.*;

public class SerializationDemo implements Serializable {
    public static void main(String[] args) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream("text.bin");
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        StudentZhang studentZhang = new StudentZhang();
        studentZhang.desc = "Zhang";
        TeacherLi teacherLi = new TeacherLi();
        teacherLi.desc = "Li";
        teacherLi.student=studentZhang;
        TeacherHuang teacherHuang = new TeacherHuang();
        teacherHuang.desc = "Huang";
        teacherHuang.student=studentZhang;
        objectOutputStream.writeObject(teacherLi);
        objectOutputStream.writeObject(teacherHuang);
        objectOutputStream.close();
    }
}

class TeacherLi implements Serializable{
    public String desc;
    public StudentZhang student;
}

class TeacherHuang implements Serializable {
    public String desc;
    public StudentZhang student;
}

class StudentZhang  implements Serializable{
    public String desc;
}
