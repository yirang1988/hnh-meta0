package hanwha.meta;

import static hanwha.util.Extend.extend;
import static org.junit.Assert.assertEquals;
import hanwha.util.IntList;
import hanwha.util.IntSet;
import hanwha.util.PairSet;
import hanwha.util.Set;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Meta {
	private static boolean     refreshing = false;
	private static AtomicInteger useCount = new AtomicInteger();

	static {
		try {
			refresh();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static   int[]    intSet; // ����-������ ����
	private static  Data[]   dataSet; // ������ ����
	private static short[][] pairSet; // ���� ¦ ����
	private static short[]  dataList; // ������ ��
	private static short[] data1List; // �㺸 ������ ��
	private static short[] data2List; // ���� ������ ��
	private static short[]   cvrList; // �㺸 �ڵ� �ε��� ��
	private static short[][]     cvr; // �㺸(�ڵ� �ε��� �� ����, ��, ������ �� ����) ��
	private static   int[]        im; // ���� �ڵ� ��

	/**
	 * �����/�غ��/����� ã�� Ű ���� ������ �����Ѵ�.
	 * @throws Exception ���̺� �б� ����, ������ �迭 ���� ũ�� ����.
	 */
	public static synchronized void refresh() throws Exception {
		IntSet     intSet0 = new  IntSet(3000);
		Set<Data> dataSet0 = new   Set<>(300);
		PairSet   pairSet0 = new PairSet(10000);
		IntList  dataList0 = new IntList(10000, 2000);
		IntList data1List0 = new IntList(10000, 3000);
		IntList data2List0 = new IntList(40000, 2000);
		IntList   cvrList0 = new IntList(30000, 1000);
		ImCvr       imCvr0 = new   ImCvr(3000);

		time(null);
		List<Row> rows = Dao.getRowList();    // Ű ���� ������ ��� �д´�
		time("Read");

		int rowCount = rows.size();           // Ű ���� ���� ��
		rows.add(new Row());                  // Ű ���� ������ ���� ǥ���Ѵ�

		for (int i = 0; i < rowCount; i++) {
			Row thisRow = rows.get(i);
			Row nextRow = rows.get(i + 1);
			dataList0.append(dataSet0.find(thisRow.data));     // ������

			if (!thisRow.sameCdNddt(nextRow)) {
				int cdNddt = intSet0.find(thisRow.cdNddt);     // ����-������
				int   data = pairSet0.find(dataList0.find());  // ������ ����Ʈ
				data1List0.append(pairSet0.find(cdNddt, data));

				if (!thisRow.sameCvrcd(nextRow)) {
					cvrList0.append(intSet0.find(thisRow.cvrCd));  // �㺸 �ڵ�
					data2List0.append(pairSet0.find(data1List0.find()));

					if (!thisRow.sameImcd(nextRow)) {
						imCvr0.append(thisRow.imCd,        // ���� �ڵ�
						              cvrList0.find(),     // �㺸 �ڵ� ����Ʈ 
						              data2List0.find());  // �㺸  ������ ����Ʈ
					}
				}
			}
		}
		time("Index");
		
		refreshing = true;
		while (0 < useCount.get()) {
			Thread.sleep(1);
		}
		intSet     =    intSet0.copy();
		dataSet    =   dataSet0.copy(); 
		pairSet    =   pairSet0.copyToShorts();
		dataList   =  dataList0.copyToShorts();
		data1List  = data1List0.copyToShorts();
		data2List  = data2List0.copyToShorts();
		cvrList    =   cvrList0.copyToShorts();
		cvr        =     imCvr0.copyCvr();
		im         =     imCvr0.copyIm();
		refreshing = false;
		time("Refresh");
		
		int totalSize = 100 * dataSet.length + 4 * (im.length + intSet.length) +
		          2 * (data2List.length +  data1List.length + dataList.length +
		          2 * pairSet[0].length + 3 * cvr[0].length +  cvrList.length);

		System.out.format("\n(%d rows --> %d KBytes)\n" +
		           "im[%d] cvr[3][%d] cvrList[%d](%d)\n" + 
		           "intSet[%d] pairSet[2][%d] dataSet[%d]\n" +
		           "dataList[%d](%d) data1List[%d](%d) data2List[%d](%d)\n",
		                   rowCount, (totalSize + 1023) / 1024, 
		   im.length, cvr[0].length, cvrList.length, cvrList0.getIndexSize(),
		              intSet.length, pairSet[0].length, dataSet.length,
		            dataList.length,  dataList0.getIndexSize(),
		           data1List.length, data1List0.getIndexSize(),
		           data2List.length, data2List0.getIndexSize());
	}

	/**
	 * ��� �ð��� ����Ѵ�.
	 * @param title ����. null�̸� ��� �ð� ���� ����.
	 */
	private static void time(String title) {
		nano = System.nanoTime();
		if (title != null) {
			System.out.format("%s(%.3f��) ", title, (nano - nano0)/1000000000.);
		}
		nano0 = nano;
	}

	private static long nano0, nano;  // ���� �� ���� ��� ����: ����, ��

	/**
	 * ���� ������ �� -- (���� �ڵ�, �㺸 �ڵ� �ε��� ��, �㺸 ������ ��)�� ��
	 */
	private static class ImCvr {
		private int[]      im;  // ���� �ڵ� �迭
		private short[][] cvr;  // �㺸(�ڵ� �ε��� �� ����, ��, ������ �� ����) �迭
		private int      size;  // ���� ��

		/**
		 * @param initialCapacity ó�� �迭 �뷮(= ���� �� �ִ밪)
		 */
		ImCvr(int initialCapacity) {
			im  = new int[initialCapacity];
			cvr = new short[3][initialCapacity];
		}

		/**
		 * ���̺� �� ������ �����͸� �ִ´�
		 * @param imCd    ���� �ڵ�
		 * @param cvrCds  �㺸 �ڵ� �ε��� �迭�� {���� �ε���, �� �ε���}
		 * @param cvrData �㺸 ������ �迭�� {���� �ε���, �� �ε���}
		 */
		void append(int imCd, int[] cvrCds, int[] cvrData) throws Exception {
			int a = cvrCds[0], b = cvrCds[1], c = cvrData[0];
			if (a < Short.MIN_VALUE || Short.MAX_VALUE < a ||
			    b < Short.MIN_VALUE || Short.MAX_VALUE < b ||
			    c < Short.MIN_VALUE || Short.MAX_VALUE < c) {
				throw new Exception("short������ ���� �� �����ϴ�.");
			}
			if (im.length <= size) {
				im  = Arrays.copyOf(im, extend(size));
				cvr = copyCvr(extend(size));
			}
			im[size]     = imCd;       // ���� �ڵ�
			cvr[0][size] = (short) a;  // �㺸 �ڵ� �ε��� �迭 ���� �ε���
			cvr[1][size] = (short) b;  // �㺸 �ڵ� �ε��� �迭 �� �ε���
			cvr[2][size] = (short) c;  // �㺸 ������ �迭 ���� �ε���
			size++;
		}

		int[] copyIm() {
			return Arrays.copyOf(im, size);
		}

		short[][] copyCvr() {
			return copyCvr(size);
		}

		private short[][] copyCvr(int newSize) {
			return new short[][] { 
				Arrays.copyOf(cvr[0], newSize),
				Arrays.copyOf(cvr[1], newSize),
				Arrays.copyOf(cvr[2], newSize)
			};
		}
	}

	private static final int       D5 = 100000, D8 = 100000000;

	private static final String[]  PREFIX = {"IA", "LA"};

	/**
	 * �����/�غ��/����� ã�� Ű �迭�� �����.
	 *
	 * @param ���̺����ڵ�  1=�����, 2=�غ��, 3=�����
	 * @param �����ڵ�           �����ڵ� ���ڿ�
	 * @param �㺸�ڵ�           �㺸�ڵ� ���ڿ�
	 * @param ��������           8�ڸ� ���� (�� 4�ڸ�, �� 2�ڸ�, �� 2�ڸ�)
	 * @param �������           Map(������� �̸�, ������� ��)
	 * @return 18�� Ű �迭
	 */
	public static Object[] getKeys(int ���̺����ڵ�,
	                               String �����ڵ�, String �㺸�ڵ�,
	                               int ��������, Map<String, Object> �������) {

		int imCd = Integer.parseInt(�����ڵ�.substring(2));   // ���� �ڵ�
		if (�����ڵ�.charAt(0) == 'L') {
			imCd += D5;    // "LA"�� �����ϴ� ���� �ڵ�
		}
		int cvrCd = Integer.parseInt(�㺸�ڵ�.substring(3));  // �㺸 �ڵ�

		Object[] keys = Data.DEFAULT_KEYS.clone();

		useCount.incrementAndGet();
		if (refreshing) {
			useCount.decrementAndGet();
			while (refreshing) {
				try { Thread.sleep(1); } catch (Exception e) {}
			}
			useCount.incrementAndGet();
		}

		do {
			int m = Arrays.binarySearch(im, imCd);  // ���� �ε���
			if (m < 0) break;  // ���� �ڵ尡 ����

			int c = IntSet.binarySearch(intSet, cvrList,
			                            cvr[0][m], cvr[1][m], cvrCd);
			if (c < 0) break;  // ���� �㺸 �ڵ尡 ����

			int data2 = data2List[cvr[2][m] + (c - cvr[0][m])];  // �㺸 ������
			int i     = pairSet[0][data2];         // �㺸 ������ ����
			int iEnd  = pairSet[1][data2];         // �㺸 ������ ��
			while (i < iEnd) {
				int  data1 = data1List[i++];       // (����-������, ������) ¦
				int cdNddt = intSet[pairSet[0][data1]];  // ����-������
				int     cd = cdNddt / D8;                // ����
				if (���̺����ڵ� == cd && �������� <= cdNddt % D8) {
					int data = pairSet[1][data1];  // ������
					int j    = pairSet[0][data];   // ������ ����
					int jEnd = pairSet[1][data];   // ������ ��
					while (j < jEnd) {
						dataSet[dataList[j++]].getKey(keys, �������);
					}
					break;
				}
				if (���̺����ڵ� < cd) break;
			}
		} while (false);

		useCount.decrementAndGet();
		return keys;
	}

	/**
	 * ���̺��� ���� ��� ���߾� �����͸� �˻��ϰų�, �����͸� ����Ѵ�.
	 *
	 * @param from  ��� ���� ���� �ε���  -- 0, 1, 2, ...
	 * @param count ��� ���� �� -- 0�̸� �����͸� ��� �˻��Ѵ�.
	 * @param file  ��� ���� �̸� -- null�̰ų� �߸��� �̸��̸� ǥ�� ������� ����Ѵ�.
	 * @throws Exception �����ͺ��̽� ���̺� �б� ����. 
	 */
	public static void testOrPrint(int from, int count, String file)
		                    throws Exception {
		boolean testing = count == 0;
		int          to = Math.min(from + count, im.length);
		PrintStream out = System.out;
		List<Row>  rows = null;

		if (testing) {
			from = 0;                 // ó������
			to   = im.length;         // ������
			rows = Dao.getRowList();  // ���̺��� ���� ������ �� ����Ʈ
		} else {
			if (file != null) {
				try {
					out = new PrintStream(file);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		int r = 0;                                  // �� �ε���
		for (int m = from; m < to; m++) {           // ���� �ε���
			for (int c0 = cvr[0][m], c = c0; c < cvr[1][m]; c++) { // �㺸 �ε���
				int data2 = data2List[cvr[2][m] + (c - c0)];       // �㺸 ������
				int i     = pairSet[0][data2];      // �㺸 ������ ����
				int iEnd  = pairSet[1][data2];      // �㺸 ������ ��
				while (i < iEnd) {
					int  data1 = data1List[i++];    // (����-������, ������) ¦
					int cdNddt = intSet[pairSet[0][data1]];  // ����-������
					int   data =        pairSet[1][data1];   // ������
					int j      = pairSet[0][data];           // ������ ����
					int jEnd   = pairSet[1][data];           // ������ �� 
					while (j < jEnd) {
						if (testing) {
							String msg = "�� �ε��� = " + r;
							Row    row = rows.get(r++);
							assertEquals(msg, row.imCd, im[m]);
							assertEquals(msg, row.cvrCd, intSet[cvrList[c]]);
							assertEquals(msg, row.cdNddt, cdNddt);
							assertEquals(msg, row.data, dataSet[dataList[j++]]);
						} else {
							out.format("%s%05d CLA%05d %d %d %s\n",
									PREFIX[im[m] / D5], im[m] % D5, // ���� �ڵ�
									intSet[cvrList[c]],       // �㺸 �ڵ�
									cdNddt / D8, cdNddt % D8, // ����, ������
									dataSet[dataList[j++]]);  // ������
						}
					}
				}
			}
		}

		if (testing) {
			assertEquals("�� ��", rows.size(), r);  // �� ���� �´��� �˻��Ѵ�
		} else {
			if (out != System.out) {
				out.close();
			}
		}
	}

	private Meta() {}
}
