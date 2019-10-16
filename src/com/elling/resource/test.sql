CREATE OR REPLACE PROCEDURE SP_INTND_SS_LOAD(I_BARE_ID IN VARCHAR2,--业务申请号
											 I_TMS IN VARCHAR2,
											 V_ERROR OUT VARCHAR2
												) AS
--业务申请信息
BEGIN SP_EXEC_REC_LOG('ABC')
SELECT COUNT(*) 
	INTO V_CNT
	FROM ONLY_SVC_GRTSTL_DTL_TBL
	WHERE WERT_METDCD_SRC IN (SELECT DISTINCT WANT_MTDCD FROM
							ONLY_SVC_GRTSTL_DTL_TBL)

	INSERT INTO  
	ONLY_SVG_INTNDHPN_EXPD_PCS_TBL
	VALUES R_INTNDHPN_EXPD_PCS_TBL;
	
	INSERT INTO  ONLN_SVC_ABC
	VALUES R_INTNDHPN_EXPD_PCS_TBL;
	
	UPDATE ONLY_SVC_INTNDHPN_EXPD_PCS_TBL
		SET MJR_INR_TXN_IND = CASE
								WHEN SF_GET_MJR_INR_TXN_IND_NFS(R_INTNDHPN_DTL_PCS_TBL.PCS_ID,'1')THEN
								'1'
								ELSE
								NULL
								END
	DELETE FROM 
	ONLY_SVC_TXN_CTL_DTL_TBL
	WHERE BAPL_ID = I_BAPL_ID
	
	WHEN MATCHED THEN
		UPDATE 
			SET B.CLT_AMT = 
				(CASE 
					WHEN B.CLT_TPCD = '001' THEN
					'002'
				END)	
	COMMIT;	
	DELETE FROM SYS_DEL_LOG ;
	UPDATE TABLE_ABC SET A = '1'
	



		