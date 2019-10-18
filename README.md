# EllingTool  
从存储过程中取出表名-正则表达式提交方法  
1.该方法从存储过程中提取出来表，是因为业务需求，存过过程太长，每个都有两三千行，然后每个存储过程中又有一些相关联的存储过程、函数等，一行一行看，不熟悉又比较费时，所以这里就简单写个方法，将需要操作的存储过程中的表提取出来（提取出包括INSERT/UPDATE/MERGE/DELETE）,SELECT语句不用，然后取出这些涉及的表的表名注释。  
2、里面还有很多不足，请见谅  
3、下面列出提取表的思路  
  1）从数据库中取出存储过程的定义：SELECT TEXT FROM USER_SOURCE WHERE NAME = UPPER(?) AND TYPE = ? ORDER BY LINE  
  2）将取到的定义用正则表达式提取出(INSERT/UPDATE/MERGE/DELETE)表名:  
  /**  
	 * 获取存储过程、xml等xml文件中的表名  
	 * 匹配正则表达式  
	 * 正则表达式主要有这几种：  
	 * 1、取出insert中的表，  
	 *  格式如下：  
	 *  	1)INSERT INTO XXX (COL1,COL2...)  
	 *  	2)INSERT INTO XXX SELECT XXX  
	 * 	正则表达式如下：INSERT\s+INTO\s+(\b\w+\b) -> 取出group(1)  
	 *   
	 * 2、取出update中的表,  
	 *	格式如下：  
	 *		1）UPDATE XXX SET ...  
	 *		2）MERGE INTO XXX   
	 *		3）排除掉 MERGE INTO XXXX CONDITION THEN UPDATE SET...  
	 *		4）这里需要考虑update这种情况  when matched then update set a.clt_zmt = xxx...  
	 * 	正则表达式如下：  
	 * 		1）UPDATE\s+(\b\w+\b)(|\s+\b\w+\b)\s+SET  ->取出group(1)  
	 * 		2）MERGE\s+INTO\s+(\b\w+\b) ->取出group(1)  
	 *   
	 * 3、取出delete中的表  
	 * 	格式如下：1）DELETE FROM XXX WHERE ....  
	 * 	正则表达式如下：DELETE\s+FROM\s+(\b\w+\b)	->取出group(1)  
	 *   
	 * 4、取出存储过程/函数  
	 * 	格式如下：  
	 * 		1）SP_XXX_XXX  
	 * 		2）SF_XXX_XXX  
	 * 	正则表达式:  
	 * 		(SP_\w+\b)  ->取出group(1)  
	 * 		(SF_\w+\b)  ->取出group(1)  
	 */  
	3）取select的正则表达式  
	/**  
	 * 正则表达式  
	 * 1、取出select中的表  
	 * 	格式如下：  
	 * 	1）SELECT XXX FROM SYS_DICT A,SYS_USER B,SYS_CONFIG C...  
	 * 	2）SELECT XXX FROM SYS_DICT A,SYS_USER B LEFT JOIN SYS_MENU M ON M.ID = A.ID....  
	 *   
	 * 2、正则表达式说明如下：分两步走，第一步找出长串，第二步根据逗号分割，第三步在解析对应的表出来  
	 *  1)第一步正则从FROM后面开始( A , XXX)由多个这个组成  
	 *  2)第二步把整个大串整个取出来,使用逗号分隔  
	 *  3)第三步，取出具体的表  
	 * note:  
	 * 	1）这里有个问题就是如果是会出换行符就有点搞不定了，一个好的方法就是解析StringBuffer的时候，不加\n\t，连续读取到内存中  
	 * 	2）去掉delete from后的表（其实这个不去掉也可以的，反正都是表，多一个也无所谓）  
	 * 3、正则表达式如下  
	 * 	第一步：(?<!DELETE)\s+FROM\s+((\b\w+\b)(\s*\b\w+\b\s*,\s*(\b\w+\b))*)|(LEFT|RIGHT|INNER)\s+JOIN\s+(\b\w+\b)  
	 * 	第二步：SYS_DICT A,SYS_USER B,SYS_CONFIG C   -> group(1).toString().split(",");  
	 *  第三步：\s*(\b\w+\b)  ->group(1)  
	 *  
	 */