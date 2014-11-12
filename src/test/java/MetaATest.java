import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;
import hanwha.meta.Meta;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class MetaATest {
	@Parameters
	public static Collection<Object[]> cases() {
		return Arrays.asList(new Object[][] {
{3, "LA00369", "CLA00223", 20130501, "����Ⱓ=15 ���ԱⰣ=5 ���ⱸ���ڵ�=02 �����ֱ��ڵ�=00",
 new Object[] {"*", "*", "*", 15, 5, "*", "*", "*", "*", "*", "*", "02", 0, "*", "*", 0, "00", 0}},
{3, "LA00369", "CLA00223", 20140301, "����Ⱓ=15 ���ԱⰣ=5 ���ⱸ���ڵ�=02 �����ֱ��ڵ�=01",
 new Object[] {"*", "*", "*", 15, 5, "*", "*", "*", "*", "*", "*", "02", 0, "*", "*", 0, "*", 0}},
{1, "LA00566", "CLA00029", 20120708, "�������ڵ�=002 �����ڵ�=01 ����Ⱓ=10 ���ԱⰣ=5 ���ⱸ���ڵ�=02 ����=35 �����ֱ��ڵ�=03",
 new Object[] {"002", "01", "*", 10, 5, "*", "*", "*", "*", "*", "*", "02", 0, "*", "*", 35, "03", 0}},
{1, "LA00566", "CLA00029", 20120708, "�������ڵ�=001 �����ڵ�=01 ����Ⱓ=10 ���ԱⰣ=5 ���ⱸ���ڵ�=02 ����=35 �����ֱ��ڵ�=03",
 new Object[] {"001", "01", "*", 10, 5, "*", "*", "*", "*", "*", "*", "02", 0, "*", "*", 0, "03", 0}}
		});
	}

	private int      ���̺����ڵ�;
	private String   �����ڵ�;
	private String   �㺸�ڵ�;
	private int      ��������;
	private String   �������;
	private Object[] ����_Ű;

	public MetaATest(int ���̺����ڵ�, String �����ڵ�, String �㺸�ڵ�,
			         int ��������, String �������, Object[] ����_Ű) {
		this.�����ڵ� = �����ڵ�;
		this.�㺸�ڵ� = �㺸�ڵ�;
		this.���̺����ڵ� = ���̺����ڵ�;
		this.�������� = ��������;
		this.������� = �������;
		this.����_Ű = ����_Ű;
	}

	@BeforeClass
	public static void setup() throws Exception {
		Class.forName(Meta.class.getName());
	}

	@Test
	public void test() {
		Map<String, Object> map = new HashMap<>();  // ������
		String[] a = �������.split("(\\s|=|,)+");
		if (a.length % 2 != 0) {
			fail("��� ������ �߸��� �ֽ��ϴ�.");
		}
		for (int i = 0; i < a.length; i += 2) {
			map.put(a[i], a[i + 1]);
		}
		assertArrayEquals(����_Ű, Meta.getKeys(���̺����ڵ�, �����ڵ�, �㺸�ڵ�, ��������, map));
	}
}
