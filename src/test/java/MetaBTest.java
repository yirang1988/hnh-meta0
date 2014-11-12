import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import hanwha.meta.Meta;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class MetaBTest {
	@Parameters
	public static Collection<Object[]> cases() {
		return Arrays.asList(new Object[][] {
{"3 LA00369 CLA00223 20130501 ����Ⱓ=15 ���ԱⰣ=5 ���ⱸ���ڵ�=02 �����ֱ��ڵ�=00",
 "* * * 15 5 * * * * * * 02 0 * * 0 00 0"},
{"3 LA00369 CLA00223 20140301 ����Ⱓ=15 ���ԱⰣ=5 ���ⱸ���ڵ�=02 �����ֱ��ڵ�=01",
 "* * * 15 5 * * * * * * 02 0 * * 0 * 0"},
{"1 LA00566 CLA00029 20120708 �������ڵ�=002 �����ڵ�=01 ����Ⱓ=10 ���ԱⰣ=5 ���ⱸ���ڵ�=02 ����=35 �����ֱ��ڵ�=03",
 "002 01 * 10 5 * * * * * * 02 0 * * 35 03 0"},
{"1 LA00566 CLA00029 20120708 �������ڵ�=001 �����ڵ�=01 ����Ⱓ=10 ���ԱⰣ=5 ���ⱸ���ڵ�=02 ����=35 �����ֱ��ڵ�=03",
 "001 01 * 10 5 * * * * * * 02 0 * * 0 03 0"}
		});
	}

	private String ����, ��;

	public MetaBTest(String ����, String ��) {
		this.���� = ����;  // ���̺����ڵ�   �����ڵ�  �㺸�ڵ�  ��������  [������]... 
		this.�� = ��;
	}

	@BeforeClass
	public static void setup() throws Exception {
		Class.forName(Meta.class.getName());
	}

	@Test
	public void test() {
		String[] a = ����.split("(\\s|,|=)+");
		if (a.length < 4 || a.length % 2 != 0) {
			fail("�����Ͱ� �����ϰų� ��� ���׿� �߸��� �ֽ��ϴ�.");
		}
		Map<String, Object> map = new HashMap<>();  // ������
		for (int i = 4; i < a.length; i += 2) {
			map.put(a[i], a[i + 1]);
		}
		Object[] keys = Meta.getKeys(Integer.parseInt(a[0]), a[1], a[2], 
		                             Integer.parseInt(a[3]), map);
		assertEquals(��, StringUtils.join(keys, " "));
	}
}
