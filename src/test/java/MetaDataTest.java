import hanwha.meta.Meta;

import org.junit.BeforeClass;
import org.junit.Test;

public class MetaDataTest {
	@BeforeClass
	public static void setup() throws Exception {
		Class.forName(Meta.class.getName());
	}

//	@Ignore
	@Test
	public void test() throws Exception {
		Meta.testOrPrint(0, 0, null);         // �׽�Ʈ
		Meta.testOrPrint(30, 1, null);        // 31��° ������� 1���� �ֿܼ� ���
		Meta.testOrPrint(30, 2, "rows.txt");  // 31��° ������� 2���� ���Ͽ� ���
	}
}
