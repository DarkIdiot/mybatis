package org.apache.ibatis.submitted.serializecircular;

import org.junit.AfterClass;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;

public class IO_SerialTest {

    // 基本测试
    @Test
    public void BasicTest() throws FileNotFoundException, IOException, ClassNotFoundException {
        // 系统默认序列化,默认writeObject、readObject
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("object.txt"));
        ArrayList<PersonOne> lst = new ArrayList<PersonOne>();
        {
            lst.add(new PersonOne("小张", 22));
            lst.add(new PersonOne("小李", 23));
            // 以下两次输出同一个对象lst
            oos.writeObject(lst);
            lst.get(0).setAge(11);
            lst.remove(1);
            oos.writeObject(lst);

            oos.writeObject(new PersonOne("小王", 24));
        }

        // 反序列化
        {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("object.txt"));
            lst = (ArrayList<PersonOne>) ois.readObject();
            System.out.println(lst.size());// 2
            lst = (ArrayList<PersonOne>) ois.readObject();
            System.out.println(lst.size());// 2
            System.out.println(lst.get(0).getAge());// 22
            // 从上面两次输出反应的都是lst改变前的状态，说明重复序列化同一个对象时只在第一次把对象转换成字节序列。

            PersonOne p = (PersonOne) ois.readObject();
            System.out.printf("%s %s %s %s\n", p.getAge(), p.getName(), p.getHome(), p.getNumber());// transient修饰的home字段为null说明没有被序列化
        }
    }

    // 自定义序列化测试1
    @Test
    public void customSerialTestOne() throws FileNotFoundException, IOException, ClassNotFoundException {
        // -----自定义序列化：写入、读出
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("object.txt"));
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("object.txt"));
        // 写
        PersonTypeOne ptt = new PersonTypeOne("小虎", 11);
        oos.writeObject(ptt);
        // 读
        ptt = (PersonTypeOne) ois.readObject();
        System.out.println(ptt.getAge() + " " + ptt.getName());
    }

    // 自定义序列化测试2
    @Test
    public void customSerialTestTwo() throws FileNotFoundException, IOException, ClassNotFoundException {
        // -----自定义序列化：对象替换writeReplace
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("object.txt"));
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("object.txt"));

        // 写，调用PersonTypeTwo的writeReplace方法，对象替换为String
        PersonTypeTwo ptt = new PersonTypeTwo("小虎", 11);
        oos.writeObject(ptt);

        // 读，已是ArrayList类型
        ArrayList<Object> pttList = (ArrayList<Object>) ois.readObject();
        System.out.println(pttList.get(0) + " " + pttList.get(1));
    }

    // 自定义序列化测试3
    @Test
    public void customSerialTestThree() throws FileNotFoundException, IOException, ClassNotFoundException {
        // -----自定义序列化：自定义enum的问题的解决readResolve
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("object.txt"));
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("object.txt"));

        // 没有readResolve
        oos.writeObject(Season_0.SPRING);
        System.out.println(((Season_0) ois.readObject()) == Season_0.SPRING);// false，可见反序列化后得到的并不是我们希望的枚举常量SPRING
        // 有readResolve
        oos.writeObject(Season_1.SPRING);
        System.out.println(((Season_1) ois.readObject()) == Season_1.SPRING);// true，通过readResolve解决上述问题
    }

    // 自定义序列化测试4
    @Test
    public void customSerialTestFour() throws FileNotFoundException, IOException, ClassNotFoundException {
        // 通过实现Externalizable接口实现的序列化，必须实现两个抽象方法，其他的用法与Serializable一样。
        // 使用很少，一般通过实现Serializable接口实现序列化
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("object.txt"));
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("object.txt"));
        // 写
        PersonTypeThree ptt = new PersonTypeThree("xiaozhou", 30);
        oos.writeObject(ptt);

        // 读
        ptt = (PersonTypeThree) ois.readObject();
        System.out.println(ptt.getAge() + " " + ptt.getName());
    }

    @AfterClass
    public static void cleanDumyFile() {
        final File file = new File("object.txt");
        if (file.exists()) {
            file.delete();
        }
    }
}

class PersonOne implements java.io.Serializable {

    /**
     * 默认序列化，一个个字段输出，读入时再按同样顺序一个个读入
     */
    private static final long serialVersionUID = -2032185216332524429L;
    private String name;
    private int age;
    transient private String home = "北京";
    private static int number = 3;// 静态属性不会被序列化，因为本身就在类中

    public PersonOne(String name, int age) {
        this.age = age;
        this.name = name;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String toString() {
        return name + " " + age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public static int getNumber() {
        return number;
    }

    public static void setNumber(int number) {
        PersonOne.number = number;
    }
}

class PersonTypeOne implements java.io.Serializable {
    /**
     * 自定义序列化，可以自己确定什么顺序输出哪些东西，反序列化时再同序读入，否则出错<br/>
     * private void writeObject(java.io.ObjectOutputStream out)<br>
     * private void readObject(java.io.ObjectInputStream in)<br>
     * private void readObjectNoData()<br>
     */
    private static final long serialVersionUID = 5990380364728171582L;
    private String name;
    private int age;

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeInt(age);
        out.writeObject(new StringBuffer(name).reverse());// 反转name
    }

    private void readObject(java.io.ObjectInputStream in) throws ClassNotFoundException, IOException {
        // 所读取的内容的顺序须与写的一样，否则出错
        this.age = in.readInt();
        this.name = ((StringBuffer) in.readObject()).reverse().toString();
    }

    public PersonTypeOne(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

}

class PersonTypeTwo implements java.io.Serializable {
    /**
     * 自定义序列化，写入时对象替换。系统在序列化某个对象前会自动先后调用writeReplace、writeObject方法<br>
     * ANY-ACCESS-MODIFIER Object writeReplace()
     */
    private static final long serialVersionUID = -7958822199382954305L;
    private String name;
    private int age;

    // private void writeReplace() {
    // // java序列化机制在序列化对象前总先调用该对象的writeReplace方法，若方法返回另一java对象则转为序列化该对象否则序列化原对象
    // }

    private Object writeReplace() {
        // java序列化机制在序列化对象前总先调用该对象的writeReplace方法，若方法返回另一java对象则转为序列化该对象否则序列化原对象
        ArrayList<Object> al = new ArrayList<Object>();
        al.add(age);
        al.add(name);
        return al;
    }

    public PersonTypeTwo(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}

class Season_0 implements java.io.Serializable {
    // readResolve() test
    private int id;

    private Season_0(int id) {
        this.id = id;
    }

    public static final Season_0 SPRING = new Season_0(1);
    public static final Season_0 SUMMER = new Season_0(2);
    public static final Season_0 FALL = new Season_0(3);
    public static final Season_0 WINTER = new Season_0(4);
}

class Season_1 implements java.io.Serializable {
    // readResolve() test
    /**
     * 与上面的Season_0相比，多了readResolve方法。<br>
     * 反序列化机制会在调用readObject后调用readResolve方法， 与writeReplace相对应<br>
     * ANY-ACCESS-MODIFIER Object readResolve()
     */
    private int id;

    private Season_1(int id) {
        this.id = id;
    }

    public static final Season_1 SPRING = new Season_1(1);
    public static final Season_1 SUMMER = new Season_1(2);
    public static final Season_1 FALL = new Season_1(3);
    public static final Season_1 WINTER = new Season_1(4);

    private Object readResolve() {
        // 系统会在调用readObject后调用此方法
        switch (id) {
            case 1:
                return SPRING;
            case 2:
                return SUMMER;
            case 3:
                return FALL;
            case 4:
                return WINTER;
            default:
                return null;
        }
    }
}

class PersonTypeThree implements java.io.Externalizable {
    private String name;
    private int age;

    public PersonTypeThree() {
        // 继承Externalizable实现的序列化必须要有此构造方法
    }

    public PersonTypeThree(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    // 通过实现Externalizable接口实现的序列化必须实现如下两个方法，其他的用法与Serializable一样。
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        // TODO Auto-generated method stub
        out.writeObject(new StringBuffer(name).reverse());
        out.writeInt(age);
        System.out.println("writeExternal run");
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        // TODO Auto-generated method stub
        this.name = ((StringBuffer) in.readObject()).reverse().toString();
        this.age = in.readInt();
        System.out.println("readExternal run");
    }
}