package kakao.test;
import kakao.data.Data;
import java.io.IOException;
import org.la4j.vector.dense.BasicVector;

public class Test {
	public static void main(String[] args) {
		
		Data test = new Data();
		

		try {
			test.load("test.libsvm");
		} catch (IOException e) {
			System.out.println(e);
		}
	}
}
