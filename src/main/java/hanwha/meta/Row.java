package hanwha.meta;

/**
 * �����ڵ�, �㺸�ڵ�, ����_�����ϰ� �װͿ� �ش��ϴ� ������
 */
class Row {
	int  imCd, cvrCd, cdNddt;  // �����ڵ�, �㺸�ڵ�, ����_������
	Data data;                 // ������

	// ���̺��а� �������� ������?
	boolean sameCdNddt(Row o) {
		return cdNddt == o.cdNddt && cvrCd == o.cvrCd && imCd == o.imCd;
	}

	// �㺸�ڵ尡 ������?
	boolean sameCvrcd(Row o) {
		return cvrCd == o.cvrCd && imCd == o.imCd;
	}

	// �����ڵ尡 ������?
	boolean sameImcd(Row o) {
		return imCd == o.imCd;
	}
}
