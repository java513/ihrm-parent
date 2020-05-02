package com.ihrm.employee.controller;

import com.ihrm.common.controller.BaseController;
import com.ihrm.common.entity.PageResult;
import com.ihrm.common.entity.Result;
import com.ihrm.common.entity.ResultCode;
import com.ihrm.common.poi.ExcelExportUtil;
import com.ihrm.common.utils.BeanMapUtils;
import com.ihrm.domain.employee.*;
import com.ihrm.domain.employee.response.EmployeeReportResult;
import com.ihrm.employee.service.*;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/employees")
public class EmployeeController extends BaseController {
    @Autowired
    private UserCompanyPersonalService userCompanyPersonalService;
    @Autowired
    private UserCompanyJobsService userCompanyJobsService;
    @Autowired
    private ResignationService resignationService;
    @Autowired
    private TransferPositionService transferPositionService;
    @Autowired
    private PositiveService positiveService;
    @Autowired
    private ArchiveService archiveService;

    /**
     * 打印员工pdf报表
     *
     */
    @RequestMapping(value = "/{id}/pdf",method = RequestMethod.GET)
    public void pdf(@PathVariable String id)throws Exception{
        //1.引入jasper文件
        Resource resource = new ClassPathResource("templates/profile.jasper");
        FileInputStream fis = new FileInputStream(resource.getFile());
        //2.构造数据
        //a.用户详情
        UserCompanyPersonal personal = userCompanyPersonalService.findById(id);
        //b.用户岗位信息
        UserCompanyJobs jobs = userCompanyJobsService.findById(id);
        //c.用户头像
        String staffPhoto = "http://q3f0mas4v.bkt.clouddn.com/"+id;
        //3.填充pdf模板数据，并输出pdf
        Map params  = new HashMap();
        Map<String, Object> map1 = BeanMapUtils.beanToMap(personal);
        Map<String, Object> map2 = BeanMapUtils.beanToMap(jobs);
        params.putAll(map1);
        params.putAll(map2);
        params.put("staffPhoto",staffPhoto);
        ServletOutputStream os = response.getOutputStream();
        try {
            JasperPrint jasperPrint = JasperFillManager.fillReport(fis, params, new JREmptyDataSource());
            //将jasperPrint以pdf形式输出
            JasperExportManager.exportReportToPdfStream(jasperPrint,os);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            os.flush();
        }
    }

    /**
     * 员工个人信息保存
     */
    @RequestMapping(value = "/{id}/personalInfo", method = RequestMethod.PUT)
    public Result savePersonalInfo(@PathVariable(name = "id") String uid, @RequestBody Map map) throws Exception {
        UserCompanyPersonal sourceInfo = BeanMapUtils.mapToBean((Map<String, Object>) map, UserCompanyPersonal.class);
        if (sourceInfo == null) {
            sourceInfo = new UserCompanyPersonal();
        }
        sourceInfo.setUserId(uid);
        sourceInfo.setCompanyId(super.companyId);
        userCompanyPersonalService.save(sourceInfo);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 员工个人信息读取
     */
    @RequestMapping(value = "/{id}/personalInfo", method = RequestMethod.GET)
    public Result findPersonalInfo(@PathVariable(name = "id") String uid) throws Exception {
        UserCompanyPersonal info = userCompanyPersonalService.findById(uid);
        if (info == null) {
            info = new UserCompanyPersonal();
            info.setUserId(uid);
        }
        return new Result(ResultCode.SUCCESS, info);
    }

    /**
     * 员工岗位信息保存
     */
    @RequestMapping(value = "/{id}/jobs", method = RequestMethod.PUT)
    public Result saveJobsInfo(@PathVariable(name = "id") String uid, @RequestBody UserCompanyJobs sourceInfo) throws Exception {
        //更新员工岗位信息
        if (sourceInfo == null) {
            sourceInfo = new UserCompanyJobs();
            sourceInfo.setUserId(uid);
            sourceInfo.setCompanyId(super.companyId);
        }
        userCompanyJobsService.save(sourceInfo);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 员工岗位信息读取
     */
    @RequestMapping(value = "/{id}/jobs", method = RequestMethod.GET)
    public Result findJobsInfo(@PathVariable(name = "id") String uid) throws Exception {
        UserCompanyJobs info = userCompanyJobsService.findById(uid);
        if (info == null) {
            info = new UserCompanyJobs();
            info.setUserId(uid);
            info.setCompanyId(companyId);
        }
        return new Result(ResultCode.SUCCESS, info);
    }

    /**
     * 离职表单保存
     */
    @RequestMapping(value = "/{id}/leave", method = RequestMethod.PUT)
    public Result saveLeave(@PathVariable(name = "id") String uid, @RequestBody EmployeeResignation resignation) throws Exception {
        resignation.setUserId(uid);
        resignationService.save(resignation);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 离职表单读取
     */
    @RequestMapping(value = "/{id}/leave", method = RequestMethod.GET)
    public Result findLeave(@PathVariable(name = "id") String uid) throws Exception {
        EmployeeResignation resignation = resignationService.findById(uid);
        if (resignation == null) {
            resignation = new EmployeeResignation();
            resignation.setUserId(uid);
        }
        return new Result(ResultCode.SUCCESS, resignation);
    }

    /**
     * 导入员工
     */
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public Result importDatas(@RequestParam(name = "file") MultipartFile attachment) throws Exception {
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 调岗表单保存
     */
    @RequestMapping(value = "/{id}/transferPosition", method = RequestMethod.PUT)
    public Result saveTransferPosition(@PathVariable(name = "id") String uid, @RequestBody EmployeeTransferPosition transferPosition) throws Exception {
        transferPosition.setUserId(uid);
        transferPositionService.save(transferPosition);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 调岗表单读取
     */
    @RequestMapping(value = "/{id}/transferPosition", method = RequestMethod.GET)
    public Result findTransferPosition(@PathVariable(name = "id") String uid) throws Exception {
        UserCompanyJobs jobsInfo = userCompanyJobsService.findById(uid);
        if (jobsInfo == null) {
            jobsInfo = new UserCompanyJobs();
            jobsInfo.setUserId(uid);
        }
        return new Result(ResultCode.SUCCESS, jobsInfo);
    }

    /**
     * 转正表单保存
     */
    @RequestMapping(value = "/{id}/positive", method = RequestMethod.PUT)
    public Result savePositive(@PathVariable(name = "id") String uid, @RequestBody EmployeePositive positive) throws Exception {
        positiveService.save(positive);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 转正表单读取
     */
    @RequestMapping(value = "/{id}/positive", method = RequestMethod.GET)
    public Result findPositive(@PathVariable(name = "id") String uid) throws Exception {
        EmployeePositive positive = positiveService.findById(uid);
        if (positive == null) {
            positive = new EmployeePositive();
            positive.setUserId(uid);
        }
        return new Result(ResultCode.SUCCESS, positive);
    }

    /**
     * 历史归档详情列表
     */
    @RequestMapping(value = "/archives/{month}", method = RequestMethod.GET)
    public Result archives(@PathVariable(name = "month") String month, @RequestParam(name = "type") Integer type) throws Exception {
        System.out.println(month);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 归档更新
     */
    @RequestMapping(value = "/archives/{month}", method = RequestMethod.PUT)
    public Result saveArchives(@PathVariable(name = "month") String month) throws Exception {
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 历史归档列表
     */
    @RequestMapping(value = "/archives", method = RequestMethod.GET)
    public Result findArchives(@RequestParam(name = "pagesize") Integer pagesize, @RequestParam(name = "page") Integer page, @RequestParam(name = "year") String year) throws Exception {
        Map map = new HashMap();
        map.put("year", year);
        map.put("companyId", companyId);
        Page<EmployeeArchive> searchPage = archiveService.findSearch(map, page, pagesize);
        PageResult<EmployeeArchive> pr = new PageResult(searchPage.getTotalElements(), searchPage.getContent());
        return new Result(ResultCode.SUCCESS, pr);
    }

    /**
     * 当月人事报表导出
     *
     * @param month
     */
    /*@RequestMapping(value = "/export/{month}", method = RequestMethod.GET)
    public void export(@PathVariable String month) throws Exception{
        System.out.println("人事报表导出"+month);
        //1.构造数据
        List<EmployeeReportResult> list = userCompanyPersonalService.findByExport(companyId, month);
        System.out.println(list.size());
        //2.创建工作簿
        XSSFWorkbook workbook = new XSSFWorkbook();
        //3.构造sheet
        XSSFSheet sheet = workbook.createSheet();
        //创建行
        String[] titles = "编号,姓名,手机,最高学历,国家地区,护照号,籍贯,生日,属相,入职时间,离职类型,离职原因,离职时间".split(",");
        //标题
        //处理标题
        Row row = sheet.createRow(0);
        int titleIndex = 0;
        for (String title : titles) {
            Cell cell = row.createCell(titleIndex++);
            cell.setCellValue(title);
        }

        //填充内容
        int rowIndex = 1;
        Cell cell = null;
        for (EmployeeReportResult employeeReportResult : list) {
            row = sheet.createRow(rowIndex++);
            // 编号,
            cell = row.createCell(0);
            cell.setCellValue(employeeReportResult.getUserId());
            // 姓名,
            cell = row.createCell(1);
            cell.setCellValue(employeeReportResult.getUsername());
            // 手机,
            cell = row.createCell(2);
            cell.setCellValue(employeeReportResult.getMobile());
            // 最高学历,
            cell = row.createCell(3);
            cell.setCellValue(employeeReportResult.getTheHighestDegreeOfEducation());
            // 国家地区,
            cell = row.createCell(4);
            cell.setCellValue(employeeReportResult.getNationalArea());
            // 护照号,
            cell = row.createCell(5);
            cell.setCellValue(employeeReportResult.getPassportNo());
            // 籍贯,
            cell = row.createCell(6);
            cell.setCellValue(employeeReportResult.getNativePlace());
            // 生日,
            cell = row.createCell(7);
            cell.setCellValue(employeeReportResult.getBirthday());
            // 属相,
            cell = row.createCell(8);
            cell.setCellValue(employeeReportResult.getZodiac());
            // 入职时间,
            cell = row.createCell(9);
            cell.setCellValue(employeeReportResult.getTimeOfEntry());
            // 离职类型,
            cell = row.createCell(10);
            cell.setCellValue(employeeReportResult.getTypeOfTurnover());
            // 离职原因,
            cell = row.createCell(11);
            cell.setCellValue(employeeReportResult.getReasonsForLeaving());
            // 离职时间
            cell = row.createCell(12);
            cell.setCellValue(employeeReportResult.getResignationTime());
        }
        //3.完成下载
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        new DownloadUtils().download(bos,response,month+"人事报表.xlsx");
    }*/

    /**
     * 模板打印
     * @param month
     * @throws Exception
     */
    /*@RequestMapping(value = "/export/{month}", method = RequestMethod.GET)
    public void export(@PathVariable String month) throws Exception{
        //1.构造数据
        List<EmployeeReportResult> list = userCompanyPersonalService.findByExport(companyId, month);
        System.out.println(list.size());
        //2.加载模板流数据
        Resource resource = new ClassPathResource("excel-template/hr-demo.xlsx");
        FileInputStream fis = new FileInputStream(resource.getFile());
        //3.根据文件流，加载指定的工作簿
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        //4.读取工作表
        Sheet sheet = workbook.getSheetAt(0);
        //5.抽取公共的样式
        Row row = sheet.getRow(2);
        CellStyle[] cellStyles = new CellStyle[(row.getLastCellNum())];
        for (int i = 0; i < row.getLastCellNum(); i++) {
            cellStyles[i] = row.getCell(i).getCellStyle();
        }
        //6.构造每行和单元格数据

        //填充内容
        int rowIndex = 2;
        Cell cell = null;
        for (EmployeeReportResult employeeReportResult : list) {
            row = sheet.createRow(rowIndex++);
            // 编号,
            cell = row.createCell(0);
            cell.setCellValue(employeeReportResult.getUserId());
            cell.setCellStyle(cellStyles[0]);
            // 姓名,
            cell = row.createCell(1);
            cell.setCellValue(employeeReportResult.getUsername());
            cell.setCellStyle(cellStyles[1]);
            // 手机,
            cell = row.createCell(2);
            cell.setCellValue(employeeReportResult.getMobile());
            cell.setCellStyle(cellStyles[2]);
            // 最高学历,
            cell = row.createCell(3);
            cell.setCellValue(employeeReportResult.getTheHighestDegreeOfEducation());
            cell.setCellStyle(cellStyles[3]);
            // 国家地区,
            cell = row.createCell(4);
            cell.setCellValue(employeeReportResult.getNationalArea());
            cell.setCellStyle(cellStyles[4]);
            // 护照号,
            cell = row.createCell(5);
            cell.setCellValue(employeeReportResult.getPassportNo());
            cell.setCellStyle(cellStyles[5]);
            // 籍贯,
            cell = row.createCell(6);
            cell.setCellValue(employeeReportResult.getNativePlace());
            cell.setCellStyle(cellStyles[6]);
            // 生日,
            cell = row.createCell(7);
            cell.setCellValue(employeeReportResult.getBirthday());
            cell.setCellStyle(cellStyles[7]);
            // 属相,
            cell = row.createCell(8);
            cell.setCellValue(employeeReportResult.getZodiac());
            cell.setCellStyle(cellStyles[8]);
            // 入职时间,
            cell = row.createCell(9);
            cell.setCellValue(employeeReportResult.getTimeOfEntry());
            cell.setCellStyle(cellStyles[9]);
            // 离职类型,
            cell = row.createCell(10);
            cell.setCellValue(employeeReportResult.getTypeOfTurnover());
            cell.setCellStyle(cellStyles[10]);
            // 离职原因,
            cell = row.createCell(11);
            cell.setCellValue(employeeReportResult.getReasonsForLeaving());
            cell.setCellStyle(cellStyles[11]);
            // 离职时间
            cell = row.createCell(12);
            cell.setCellValue(employeeReportResult.getResignationTime());
            cell.setCellStyle(cellStyles[12]);
        }
        //3.完成下载
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        new DownloadUtils().download(bos,response,month+"人事报表.xlsx");
    }*/

    /**
     * 使用工具类实现excel导出
     * @param month
     * @throws Exception
     */
    @RequestMapping(value = "/export/{month}", method = RequestMethod.GET)
    public void export(@PathVariable String month) throws Exception{
        System.out.println(month);
        //1.构造数据
        List<EmployeeReportResult> list = userCompanyPersonalService.findByExport(companyId, month);
        System.out.println(list.size());
        //2.加载模板流数据
        Resource resource = new ClassPathResource("excel-template/hr-demo.xlsx");
        FileInputStream fis = new FileInputStream(resource.getFile());
       //3.通过工具类导出excel
        new ExcelExportUtil(EmployeeReportResult.class,2,2).
                export(response,fis,list,month+"人事报表.xlsx");
    }

    /**
     * 使用sxssf实现百万数据报表导出
     *
     * @param month
     * @throws Exception
     */
    /*@RequestMapping(value = "/export/{month}", method = RequestMethod.GET)
    public void export(@PathVariable String month) throws Exception {
        System.out.println("人事报表导出" + month);
        //1.构造数据
        List<EmployeeReportResult> list = userCompanyPersonalService.findByExport(companyId, month);
        System.out.println(list.size());
        //2.创建工作簿
//        XSSFWorkbook workbook = new XSSFWorkbook();
        SXSSFWorkbook workbook = new SXSSFWorkbook(100);
        //3.构造sheet
        Sheet sheet = workbook.createSheet();
        //创建行
        String[] titles = "编号,姓名,手机,最高学历,国家地区,护照号,籍贯,生日,属相,入职时间,离职类型,离职原因,离职时间".split(",");
        //标题
        //处理标题
        Row row = sheet.createRow(0);
        int titleIndex = 0;
        for (String title : titles) {
            Cell cell = row.createCell(titleIndex++);
            cell.setCellValue(title);
        }

        //填充内容
        int rowIndex = 1;
        Cell cell = null;
        for (int i = 0; i < 10000; i++) {
            for (EmployeeReportResult employeeReportResult : list) {
                row = sheet.createRow(rowIndex++);
                // 编号,
                cell = row.createCell(0);
                cell.setCellValue(employeeReportResult.getUserId());
                // 姓名,
                cell = row.createCell(1);
                cell.setCellValue(employeeReportResult.getUsername());
                // 手机,
                cell = row.createCell(2);
                cell.setCellValue(employeeReportResult.getMobile());
                // 最高学历,
                cell = row.createCell(3);
                cell.setCellValue(employeeReportResult.getTheHighestDegreeOfEducation());
                // 国家地区,
                cell = row.createCell(4);
                cell.setCellValue(employeeReportResult.getNationalArea());
                // 护照号,
                cell = row.createCell(5);
                cell.setCellValue(employeeReportResult.getPassportNo());
                // 籍贯,
                cell = row.createCell(6);
                cell.setCellValue(employeeReportResult.getNativePlace());
                // 生日,
                cell = row.createCell(7);
                cell.setCellValue(employeeReportResult.getBirthday());
                // 属相,
                cell = row.createCell(8);
                cell.setCellValue(employeeReportResult.getZodiac());
                // 入职时间,
                cell = row.createCell(9);
                cell.setCellValue(employeeReportResult.getTimeOfEntry());
                // 离职类型,
                cell = row.createCell(10);
                cell.setCellValue(employeeReportResult.getTypeOfTurnover());
                // 离职原因,
                cell = row.createCell(11);
                cell.setCellValue(employeeReportResult.getReasonsForLeaving());
                // 离职时间
                cell = row.createCell(12);
                cell.setCellValue(employeeReportResult.getResignationTime());
            }
        }
        //3.完成下载
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        new DownloadUtils().download(bos, response, month + "人事报表.xlsx");
    }*/

}
