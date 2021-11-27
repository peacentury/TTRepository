import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TianTian {
    // MySQL 8.0 以下版本 - JDBC 驱动名及数据库 URL
    //static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    //static final String DB_URL = "jdbc:mysql://localhost:3306/Jiang";

    // MySQL 8.0 以上版本 - JDBC 驱动名及数据库 URL
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/Jiang?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    // 数据库的用户名与密码，需要根据自己的设置
    static final String USER = "root";
    static final String PASS = "Jiang@1123";


    public static void main(String[] args) throws Exception {
  /*  truncateFundsTT();
    getDataAndWrite("pg","偏股型.xlsx");
    getDataAndWrite("gp","股票型.xlsx");
    getDataAndWrite("hh","混合型.xlsx");
    getDataAndWrite("zq","债券型.xlsx");
    getDataAndWrite("zs","指数型.xlsx");
    List<String> sharesListCode=getSharesList();
    for(String fundCode:sharesListCode){
        //更新历史净值表
        getFundHisVal(fundCode);

        //更新持仓
        Map latestMarketInfo=getLatestMarketInfo(fundCode);
        String latestDate= latestMarketInfo.get("val_date").toString();

        Map shareMap=getLatestShareInfo(fundCode);
        String shareDate=shareMap.get("update_date").toString();

         if (latestDate.compareTo(shareDate)>0){
            addShare(shareMap,latestMarketInfo);
        }
    }*/
        getInvesDays("001018");
        //http://fundgz.1234567.com.cn/js/506005.js?rt=1637839867693
    }

    public static void getInvesDays(String fundCode) throws  Exception{

        Connection conn = null;
        PreparedStatement  pstmt=null;
        conn =getDataBaseConn();

        String sql = " select val_date,cell_val from fund_his_val where instr(val_date,'-18')>0 and fund_code=? order by val_date; ";
        pstmt = conn.prepareStatement(sql);
        pstmt.setString(1,fundCode);
        ResultSet rs = pstmt.executeQuery();

        Map<String,BigDecimal> dataMap= new HashMap<>();

        while (rs.next()) {
           String valDate =rs.getString(1) ;
           BigDecimal bdCellVal =rs.getBigDecimal(2) ;
           dataMap.put(valDate,bdCellVal);
           System.out.println(valDate+","+bdCellVal.toString());
        }

        sql="select val_date,cell_val,day_val_change from fund_his_val where fund_code=? order by val_date; ";
        pstmt = conn.prepareStatement(sql);
        pstmt.setString(1,fundCode);
        rs = pstmt.executeQuery();
        double d1=0;
        double d2=0;
        double d3=0;
       // double d4=0;
        while (rs.next()) {
            String valDate =rs.getString(1) ;
            BigDecimal bdCellVal =rs.getBigDecimal(2) ;

            //d4=rs.getDouble(3);
            d3=rs.getDouble(3);
            if(d3<0&&d2<0&&d1<0){
                dataMap.put(valDate,bdCellVal);
                System.out.println(valDate+","+bdCellVal.toString());
            }
            d1=d2;
            d2=d3;
            //d3=d4;
        }

        if(conn!=null) {
            conn.close();
        }

        double sumVol=0;
        for (Map.Entry<String,BigDecimal> entry : dataMap.entrySet()) {
            sumVol+=10000/entry.getValue().doubleValue();
        }

        Map latestMarketInfo=getLatestMarketInfo(fundCode);
        double cellVal=Double.valueOf(latestMarketInfo.get("cell_val").toString());
        System.out.println(dataMap.entrySet().size() * 10000+","+sumVol+","+sumVol*cellVal);
    }

    public static void getDataAndWrite(String fundType,String fileName) throws Exception {
        // 创建HttpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = null;
        CloseableHttpResponse response = null;

        //创建excel文件
        File NewxlsFile = new File("D:\\"+fileName);
        // 创建一个工作簿
        XSSFWorkbook Newworkbook = new XSSFWorkbook();
        // 创建一个工作表
        XSSFSheet Newsheet = Newworkbook.createSheet("sheet1");

        Connection conn = null;
        PreparedStatement  pstmt=null;
        String maxDate= getTTFundMaxDate();
        try{
            conn =getDataBaseConn();
            //标识是否需要更新数据
            boolean isFlag=false;
            // 将数据填入新的表格中
            for(int k=1;k<=60;k++){
             /*   httpPost = new HttpPost("https://fundapi.eastmoney.com/fundtradenew.aspx?sc=1n&st=desc&pn=100&cp=&ct=&cd=&ms=&fr=&plevel=&fst=&ftype=&fr1=&fl=0&isab=1");
                // 声明List集合,封装表单中的参数
                ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
                // 设置请求地址:http://yun.itheima.com/search?keys=Java
                params.add(new BasicNameValuePair("pi",String.valueOf(k)));
                params.add(new BasicNameValuePair("ft",fundType));
                // 创建表单的Entity对象,第一个参数就是封装好的表单数据,第二个参数就是编码
                UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params, "utf8");
                // 设置表单的Entity对象到Post请求中
                httpPost.setEntity(formEntity);*/
                //创建 URIBuilder 对象
                if(isFlag){
                    break;
                }
                URIBuilder  uriBuilder = new URIBuilder("https://fundapi.eastmoney.com/fundtradenew.aspx?sc=1n&st=desc&pn=100&cp=&ct=&cd=&ms=&fr=&plevel=&fst=&ftype=&fr1=&fl=0&isab=1");
                //设置参数
                uriBuilder.setParameter("ft",fundType);
                uriBuilder.setParameter("pi",String.valueOf(k));

                HttpGet httpGet  = new HttpGet(uriBuilder.build());
                response = httpClient.execute(httpGet);

                //response = httpClient.execute(httpPost);
                // 解析响应
                if(response.getStatusLine().getStatusCode() == 200){
                    String content = EntityUtils.toString(response.getEntity(), "utf8");
                    int index1= content.indexOf("[");
                    int index2= content.indexOf("]");
                    String datas= content.substring(index1+1,index2);
                    System.out.println(datas);
                    //如果没数据了，结束
                    if(StringUtils.isBlank(datas)){
                        break;
                    }
                    String[] funds=datas.split("\"");
                    int rowNum=0;
                    for(String fund:funds){
                        if(fund.length()<115){
                            continue;
                        }
                        fund=fund.replace(",","");
                        String[]  cells= fund.split("\\|");
                        //创建行
                        XSSFRow Newrows = Newsheet.createRow(rowNum+(k-1)*100);
                        for (int j=0;j<15;j++){
                            if(StringUtils.isBlank(cells[j])){
                                cells[j]="0";
                                Newrows.createCell(j).setCellValue(cells[j]);
                            }else{
                                if(j==0){
                                    Newrows.createCell(j).setCellValue("99"+cells[j]);
                                }else {
                                    Newrows.createCell(j).setCellValue(cells[j]);
                                }
                            }
                        }
                        //已经更新过了数据，不用重复更新
                       /* if(StringUtils.isNotBlank(maxDate)&&maxDate.compareTo(cells[3].toString())>=0){
                            isFlag=true;
                            break;
                        }*/

                        String sql = "insert into FUNDS_TT (FUND_CODE, FUND_NAME, FUND_TYPE, VAL_DATE, VAL, TODAY_DAY, LASTEST_WEEK, LASTEST_MONTH, LASTEST_3MONTH, LASTEST_6MONTH, LASTEST_YEAR, LASTEST_2YEAR, LASTEST_3YEAR, THIS_YEAR, HIS) " +
                                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

                        pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1, cells[0]);
                        pstmt.setString(2, cells[1]);
                        pstmt.setString(3, cells[2]);
                        pstmt.setString(4, cells[3]);
                        pstmt.setBigDecimal(5, BigDecimal.valueOf(Double.valueOf(cells[4])));
                        pstmt.setBigDecimal(6, BigDecimal.valueOf(Double.valueOf(cells[5])));
                        pstmt.setBigDecimal(7, BigDecimal.valueOf(Double.valueOf(cells[6])));
                        pstmt.setBigDecimal(8, BigDecimal.valueOf(Double.valueOf(cells[7])));
                        pstmt.setBigDecimal(9,BigDecimal.valueOf(Double.valueOf(cells[8])));
                        pstmt.setBigDecimal(10, BigDecimal.valueOf(Double.valueOf(cells[9])));
                        pstmt.setBigDecimal(11, BigDecimal.valueOf(Double.valueOf(cells[10])));
                        pstmt.setBigDecimal(12, BigDecimal.valueOf(Double.valueOf(cells[11])));
                        pstmt.setBigDecimal(13, BigDecimal.valueOf(Double.valueOf(cells[12])));
                        pstmt.setBigDecimal(14, BigDecimal.valueOf(Double.valueOf(cells[13])));
                        pstmt.setBigDecimal(15, BigDecimal.valueOf(Double.valueOf(cells[14])));

                        pstmt.executeUpdate();

                        rowNum++;
                    }
                }

                //将excel写入
                FileOutputStream fileOutputStream = new FileOutputStream(NewxlsFile);
                Newworkbook.write(fileOutputStream);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try{
                if(pstmt!=null) {
                    pstmt.close();
                }
            }catch(SQLException se2){
            }// 什么都不做

            try{
                if(conn!=null) {
                    conn.close();
                }
            }catch(SQLException se){
                se.printStackTrace();
            }
        }
    }

    public static void getFundHisVal(String fundCode) throws Exception {
        // 创建HttpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;

        String pageIndex = "1";
        String pageSize="100";
        String referer = "http://fundf10.eastmoney.com/";
        String time = System.currentTimeMillis()+"";
        String callback="jQuery"+time+"_"+fundCode;
        //jQuery183021911291884475825_1637225005550
        Connection conn = null;
        PreparedStatement  pstmt=null;
        int  totalCount=0;
        try{
                //创建 URIBuilder 对象
            String url = "http://api.fund.eastmoney.com/f10/lsjz";
           URIBuilder  uriBuilder = new URIBuilder(url);

            //设置参数
            uriBuilder.setParameter("callback",callback);
            uriBuilder.setParameter("fundCode",fundCode);
            uriBuilder.setParameter("pageIndex",pageIndex);
            uriBuilder.setParameter("pageSize",pageSize);
            //uriBuilder.setParameter("startDate",startTime);
            //uriBuilder.setParameter("endDate",endTime);
            uriBuilder.setParameter("_",time);

            HttpGet httpGet  = new HttpGet(uriBuilder.build());
            //有header校验
            httpGet.addHeader("Referer", referer);
            httpGet.addHeader("Host", "api.fund.eastmoney.com");
            httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36");

            response = httpClient.execute(httpGet);
            // 解析响应
            if(response.getStatusLine().getStatusCode() == 200){
                String content = EntityUtils.toString(response.getEntity(), "utf8");
                content=content.substring(content.indexOf("(")+1,content.length()-1);
                //System.out.println(content);


                JSONObject rootObject = JSONObject.parseObject(content);
                totalCount = rootObject.getIntValue("TotalCount");
                }

            //从网站获取到了数据，这更新数据
            if(totalCount>0){
                int pages =totalCount/Integer.valueOf(pageSize)+1;

                String sql = "delete from  fund_his_val where fund_code=?;";
                conn=getDataBaseConn();
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, fundCode);
                pstmt.executeUpdate();

                for(int i=1;i<=pages;i++){
                    //休眠500ms，防止请求频繁
                    Thread.sleep(500);

                     pageIndex = i+"";
                     pageSize="100";
                     referer = "http://fundf10.eastmoney.com/";
                     time = System.currentTimeMillis()+"";
                     callback="jQuery"+time+"_"+fundCode;

                    //设置参数
                    uriBuilder.setParameter("callback",callback);
                    uriBuilder.setParameter("fundCode",fundCode);
                    uriBuilder.setParameter("pageIndex",pageIndex);
                    uriBuilder.setParameter("pageSize",pageSize);
                    uriBuilder.setParameter("_",time);

                    httpGet  = new HttpGet(uriBuilder.build());
                    //有header校验
                    httpGet.addHeader("Referer", referer);
                    httpGet.addHeader("Host", "api.fund.eastmoney.com");
                    httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36");
                    response = httpClient.execute(httpGet);

                    // 解析响应
                    if(response.getStatusLine().getStatusCode() == 200){
                        String content = EntityUtils.toString(response.getEntity(), "utf8");
                        content=content.substring(content.indexOf("(")+1,content.length()-1);
                        System.out.println(content);

                        JSONObject rootObject = JSONObject.parseObject(content);
                        JSONObject dataObject = rootObject.getJSONObject("Data");
                        JSONArray  LSJZListArray = dataObject.getJSONArray("LSJZList");

                        for (int j = 0; j < LSJZListArray.size(); j++) {
                            JSONObject realData = LSJZListArray.getJSONObject(j);

                            sql = "insert into fund_his_val values(?,?,?,?,?,?,?,?);";
                            pstmt = conn.prepareStatement(sql);
                            pstmt.setString(1, fundCode);
                            pstmt.setString(2, fundCode);
                            pstmt.setString(3, realData.getString("FSRQ"));
                            pstmt.setBigDecimal(4, realData.getBigDecimal("DWJZ"));
                            pstmt.setBigDecimal(5, realData.getBigDecimal("LJJZ"));
                            pstmt.setBigDecimal(6, realData.getBigDecimal("JZZZL"));
                            pstmt.setString(7, realData.getString("SGZT"));
                            pstmt.setString(8, realData.getString("SHZT"));
                            pstmt.executeUpdate();
                        }
                    }
                }
            }
            pstmt.close();
            conn.close();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //关闭response
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Map getLatestShareInfo(String fundCode) throws  Exception{
        Connection conn = null;
        PreparedStatement  pstmt=null;
        conn =getDataBaseConn();

        String sql = "select * from shares where fund_code=? order by update_date desc limit 1;";
        pstmt = conn.prepareStatement(sql);

        pstmt.setString(1, fundCode);
        ResultSet rs = pstmt.executeQuery();

        Map retMap=new HashMap<>();
        if (rs.next()) {
            retMap.put("fund_code",rs.getString(1)) ;
            retMap.put("fund_name",rs.getString(2)) ;
            retMap.put("fund_vol",rs.getBigDecimal(3)) ;
            retMap.put("current_cost",rs.getBigDecimal(4)) ;
            retMap.put("market_value",rs.getBigDecimal(5)) ;
            retMap.put("update_date",rs.getString(6)) ;
        }

        if(conn!=null) {
            conn.close();
        }
        return retMap;
    }

    public static Map getLatestMarketInfo(String fundCode) throws  Exception{
        Map rtnMap= new HashMap();
        Connection conn = null;
        PreparedStatement  pstmt=null;
        conn =getDataBaseConn();

        String sql = "select val_date,cell_val from fund_his_val where fund_code=? order by val_date desc limit 1;";

        pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, fundCode);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            rtnMap.put("val_date",rs.getString(1))  ;
            rtnMap.put("cell_val",rs.getBigDecimal(2))  ;
        }

        if(conn!=null) {
            conn.close();
        }
        return rtnMap;
    }

    public static List<String> getSharesList() throws  Exception{
        List<String> retList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement  pstmt=null;
        conn =getDataBaseConn();

        String sql = "select distinct fund_code from shares where is_holding=1;";
        pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery(sql);

        while (rs.next()) {
            retList.add(rs.getString(1) );
        }

        if(conn!=null) {
            conn.close();
        }
        return retList;
    }

    public static String getTTFundMaxDate() throws  Exception{
        String maxDate="";
        Connection conn = null;
        PreparedStatement  pstmt=null;
        conn =getDataBaseConn();

        String sql = " select max(val_date) from FUNDS_TT where fund_code='001510';";
        pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery(sql);

        if (rs.next()) {
            maxDate=rs.getString(1) ;
        }

        if(conn!=null) {
            conn.close();
        }
        return maxDate;
    }

    public static void addShare(Map shareMap,Map marketInfoMap) throws  Exception{
        Connection conn = null;
        PreparedStatement  pstmt=null;
        conn =getDataBaseConn();

        String sql = "insert into shares(fund_code,fund_name,fund_vol,current_cost,market_value,update_date) values (?,?,?,?,?,?);";
        pstmt = conn.prepareStatement(sql);

        pstmt.setString(1, shareMap.get("fund_code").toString());
        pstmt.setString(2, shareMap.get("fund_name").toString());

        BigDecimal bdFundVol=new BigDecimal(shareMap.get("fund_vol").toString());
        pstmt.setBigDecimal(3, bdFundVol);

        BigDecimal bdCurrentCost=new BigDecimal(shareMap.get("current_cost").toString());
        pstmt.setBigDecimal(4, bdCurrentCost);

        BigDecimal bdCellVal=new BigDecimal(marketInfoMap.get("cell_val").toString());
        BigDecimal bdMarketVal =bdFundVol.multiply(bdCellVal);
        pstmt.setBigDecimal(5, bdMarketVal);

        pstmt.setString(6, marketInfoMap.get("val_date").toString());

        pstmt.executeUpdate();

        if(conn!=null) {
            conn.close();
        }
    }

    public static  void  truncateFundsTT() throws Exception{
        Connection conn = null;
        PreparedStatement  pstmt=null;
        String sql = "truncate table funds_tt;";
        conn=getDataBaseConn();
        pstmt = conn.prepareStatement(sql);
        pstmt.executeUpdate();
        if(conn!=null) {
            conn.close();
        }
    }

    public static Connection getDataBaseConn() throws Exception{
        Class.forName("com.mysql.jdbc.Driver");
        System.out.println("连接数据库...");
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

}
