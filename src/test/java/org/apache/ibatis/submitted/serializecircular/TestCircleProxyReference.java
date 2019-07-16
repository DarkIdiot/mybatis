package org.apache.ibatis.submitted.serializecircular;

import org.apache.ibatis.executor.loader.WriteReplaceInterface;
import org.junit.Test;

import java.io.*;


public class TestCircleProxyReference {

    @Test
    public void testCircleReference() throws IOException {
        Dept$1 dept$1 = new Dept$1();
        Employee employee = new Employee();
        dept$1.employee = employee;
        dept$1.id = 001;
        employee.dept = dept$1;
        employee.id = 0002;

        FileOutputStream fileOutputStream = new FileOutputStream("text.bin");
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(dept$1);
//        Dept dept = (Dept) UtilityTester.serializeAndDeserializeObject(dept$1);
//        Employee employee$1 = (Employee) UtilityTester.serializeAndDeserializeObject(employee);
    }
}

class Dept implements Serializable {
    public int id;
    public Employee employee;
}

class Dept$1 extends Dept implements WriteReplaceInterface {

    private void writeObject(ObjectOutputStream out) throws IOException, ClassNotFoundException {
        throw new ClassNotFoundException(); // 模拟动态生成的class 对象，不能被序列化与反序列化
    }

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        throw new ClassNotFoundException(); // 模拟动态生成的class 对象，不能被序列化与反序列化
    }

    @Override
    public Object writeReplace() throws IOException {
        SerializeStateHolder serialStateHolder = new SerializeStateHolder();
        Dept dept = new Dept();
        dept.employee = employee;
        dept.id = id;
        serialStateHolder.userBean = dept;
        serialStateHolder.holderHasValue = "I'm dept.";
        return serialStateHolder;
    }
}

class Employee implements Serializable {
    public int id;
    public Dept dept;
}

class Employee$1 extends Employee implements WriteReplaceInterface {

    private void writeObject(ObjectOutputStream out) throws IOException, ClassNotFoundException {
        throw new ClassNotFoundException(); // 模拟动态生成的class 对象，不能被序列化与反序列化
    }

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        throw new ClassNotFoundException(); // 模拟动态生成的class 对象，不能被序列化与反序列化
    }

    @Override
    public Object writeReplace() throws ObjectStreamException {
        SerializeStateHolder serialStateHolder = new SerializeStateHolder();
        Employee employee = new Employee();
        employee.dept = dept;
        employee.id = id;
        serialStateHolder.userBean = employee;
        serialStateHolder.holderHasValue = "I'm employee.";
        return serialStateHolder;
    }
}


class SerializeStateHolder implements Externalizable {
    private static final ThreadLocal<ObjectOutputStream> stream = new ThreadLocal<ObjectOutputStream>();

    public Object userBean;
    private byte[] userBeanBytes = new byte[0];
    public String holderHasValue;

    public SerializeStateHolder() {
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        boolean firstRound = false;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream os = stream.get();
        if (os == null) {
            os = new ObjectOutputStream(baos);
            firstRound = true;
            stream.set(os);
        }
        os.writeObject(this.userBean);
        os.writeObject(holderHasValue);
        final byte[] bytes = baos.toByteArray();
        out.writeObject(bytes);
        if (firstRound) {
            stream.remove();
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        final Object data = in.readObject();
        if (data.getClass().isArray()) {
            this.userBeanBytes = (byte[]) data;
        } else {
            this.userBean = data;
        }
    }

    protected final Object readResolve() throws ObjectStreamException {
        /* Second run */
        if (this.userBean != null && this.userBeanBytes.length == 0) {
            return this.userBean;
        }
        /* First run */
        try {
            final ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(this.userBeanBytes));
            this.userBean = in.readObject();
            this.holderHasValue = (String) in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (userBean instanceof Dept) {
            Dept$1 dept$1 = new Dept$1();
            Dept userBean = (Dept) this.userBean;
            dept$1.employee = userBean.employee;
            dept$1.id = userBean.id;
            return dept$1;
        }
        if (userBean instanceof Employee) {
            Employee$1 employee$1 = new Employee$1();
            Employee userBean = (Employee) this.userBean;
            employee$1.dept = userBean.dept;
            employee$1.id = userBean.id;
            return employee$1;
        }
        return null;
    }
}