package hanwha.meta;

import java.io.Closeable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
class Dao {
	private static final String QUERY =
"SELECT DISTINCT\n"+
"       CASE SUBSTR(A.INS_IMCD,1,2) WHEN 'IA' THEN '0' ELSE '1' END ||\n"+
"       SUBSTR(A.INS_IMCD,3) �����ڵ�\n"+
"     , SUBSTR(A.CVRCD,4)    �㺸�ڵ�\n"+
"     , A.LTPRM_BA_TABL_FLGCD||TO_CHAR(A.AP_NDDT,'YYYYMMDD') ���̺���_������\n"+
"     , CASE A.BA_TABL_IDNFR_CLMNM\n"+
"       WHEN           '�������ڵ�' THEN 0\n"+
"       WHEN             '�����ڵ�' THEN 1\n"+
"       WHEN         '�����ѵ��ڵ�' THEN 2\n"+
"       WHEN             '����Ⱓ' THEN 3  -- Number\n"+
"       WHEN             '���ԱⰣ' THEN 4  -- Number\n"+
"       WHEN         '�繰�����ڵ�' THEN 5\n"+
"       WHEN         '���������ڵ�' THEN 6\n"+
"       WHEN     '�ű԰��ű����ڵ�' THEN 7\n"+
"       WHEN         '���Ա����ڵ�' THEN 8\n"+
"       WHEN             '�����ڵ�' THEN 9\n"+
"       WHEN             '�޼��ڵ�' THEN 10\n"+
"       WHEN         '���ⱸ���ڵ�' THEN 11\n"+
"       WHEN               '������' THEN 12 -- Number\n"+
"       WHEN         '���������ڵ�' THEN 13\n"+
"       WHEN '�Ǻ����ڰ��豸���ڵ�' THEN 14\n"+
"       WHEN             '���Կ���' THEN 15 -- Number\n"+
"       WHEN         '�����ֱ��ڵ�' THEN 16\n"+
"       WHEN             '������' THEN 17 -- Number\n"+
"                                   ELSE 18 END Ű�ε���\n"+
"     , A.IDNFR_ADM_ITNM                        Ű���׸��\n"+
"     , B.CND_IT_IDNFR_CLMNM                    �����׸��\n"+
"     , B.ADMIT_AP_CND_FLGCD                    �񱳿�����\n"+
"     , CASE B.CND_IT_CNDVL WHEN '*' THEN '0'\n"+
"       ELSE B.CND_IT_CNDVL END                 ���ǰ�\n"+
"  FROM IGD_LTPRM_BA_ATR_META A LEFT JOIN\n"+
"       IGD_ADM_IT_AP_CND B\n"+
"    ON A.LTPRM_BA_TABL_FLGCD = B.LTPRM_BA_TABL_FLGCD -- ���̺����ڵ�\n"+
"   AND A.INS_IMCD            = B.INS_IMCD            -- ���������ڵ�\n"+
"   AND A.CVRCD               = B.CVRCD               -- �㺸�ڵ�\n"+
"   AND A.BA_TABL_IDNFR_CLMNM = B.ST_IT_IDNFR_CLMNM   -- Ű�ε���\n"+
"   AND A.AP_NDDT             = B.AP_NDDT             -- ������������\n"+
"   AND A.AP_STRDT            = B.AP_STRDT            -- �����������\n"+
" WHERE A.LTPRM_BA_TABL_FLGCD BETWEEN '01' AND '03'\n"+
"   AND A.BA_TABL_IDNFR_CLMNM <> 'Ű����'\n"+
"   AND A.BA_TABL_IDNFR_CLMNM <> '����Ⱓ'\n"+
"   AND A.BA_TABL_IDNFR_CLMNM NOT LIKE '% '\n"+
"   AND (B.ADMIT_AP_CND_FLGCD is null OR\n"+
"        B.ADMIT_AP_CND_FLGCD BETWEEN '01' AND '06')\n"+
" ORDER BY �����ڵ�,�㺸�ڵ�,���̺���_������, Ű�ε���,Ű���׸��,�����׸��,�񱳿�����,���ǰ�\n";

	static List<Row> getRowList() throws Exception {
		try (Closeable c = new AnnotationConfigApplicationContext(Config.class)) {
			return jdbcTemplate.query(QUERY, new RowMapper<Row>() {
				@Override
				public Row mapRow(ResultSet rs, int rowNum) throws SQLException {
					Row row    = new Row();
					row.imCd   = rs.getInt("�����ڵ�");
					row.cvrCd  = rs.getInt("�㺸�ڵ�");
					row.cdNddt = rs.getInt("���̺���_������");
					row.data   = new Data(rs.getInt("Ű�ε���"),
					                      rs.getString("Ű���׸��"),
					                      rs.getString("�����׸��"),
					                      rs.getInt("�񱳿�����"),
					                      rs.getInt("���ǰ�"));
					return row;
				}
			});
		}
	}

	@Autowired
	private void setJdbc(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	private static JdbcTemplate jdbcTemplate;
}
