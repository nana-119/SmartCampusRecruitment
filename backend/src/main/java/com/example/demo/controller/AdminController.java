package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.model.Admin;
import com.example.demo.model.Recruiter;
import com.example.demo.model.Seeker;
import com.example.demo.service.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@Controller
public class AdminController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private SeekerService seekerService;
    @Autowired
    private RecruiterService recruiterService;
    @Autowired
    private AcademicRecordService academicRecordService;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private TempAcademicRecordService tempAcademicRecordService;
    @Autowired
    private JobPositionService jobPositionService;

    @GetMapping("/login")
    public String loginPage() {
        System.out.println("访问登录页面 GET /login");
        return "login";
    }

    @PostMapping("/login/check")
    public String loginCheck(
            @RequestParam String username,
            @RequestParam String password) {

        boolean isAuthenticated = adminService.verifyAdmin(username, password);

        if (isAuthenticated) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
            return "redirect:/home";
        } else {
            return "redirect:/login?error";
        }
    }

    @GetMapping("/home")
    public String homePage() {
        return "home";
    }

    // 学生管理页面跳转
    @GetMapping("/student")
    public String studentManagement(@RequestParam(required = false) String studentId, Model model) {
        List<SeekerDTO> seekerList;

        if (studentId != null && !studentId.trim().isEmpty()) {
            // 如果提供了学号筛选条件，则按学号筛选
            seekerList = seekerService.getAllSeekers().stream()
                    .filter(seeker -> seeker.getStudentId() != null &&
                            seeker.getStudentId().contains(studentId))
                    .collect(Collectors.toList());
        } else {
            // 如果没有筛选条件，获取所有学生
            seekerList = seekerService.getAllSeekers();
        }

        model.addAttribute("seekerList", seekerList);
        // 保留筛选条件以便在页面上显示
        model.addAttribute("studentId", studentId);
        return "student";
    }

    @PostMapping("/seekerList/add")
    @ResponseBody
    public ResponseEntity<String> addStudent(@RequestBody SeekerDTO seekerDTO) {
        try {
            // 检查学号是否已存在
            if (seekerDTO.getStudentId() != null && !seekerDTO.getStudentId().trim().isEmpty()) {
                List<SeekerDTO> existingSeekers = seekerService.getAllSeekers();
                boolean exists = existingSeekers.stream()
                        .anyMatch(seeker -> seeker.getStudentId() != null &&
                                seeker.getStudentId().equals(seekerDTO.getStudentId()));

                if (exists) {
                    return ResponseEntity.badRequest().body("学号已存在");
                }
            }

            // 设置默认密码
            seekerDTO.setPassword("123456");

            // 调用服务保存学生信息
            seekerService.createSeeker(seekerDTO);
            return ResponseEntity.ok("学生添加成功");
        } catch (Exception e) {
            e.printStackTrace(); // 打印错误堆栈以便调试
            return ResponseEntity.status(500).body("添加失败: " + e.getMessage());
        }
    }

    @GetMapping("/seekerList/get")
    @ResponseBody
    public ResponseEntity<SeekerDTO> getStudent(@RequestParam String studentId) {
        try {
            List<SeekerDTO> seekers = seekerService.getAllSeekers();
            Optional<SeekerDTO> student = seekers.stream()
                    .filter(seeker -> seeker.getStudentId() != null && seeker.getStudentId().equals(studentId))
                    .findFirst();

            if (student.isPresent()) {
                return ResponseEntity.ok(student.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    // 更新学生信息接口
    @PostMapping("/seekerList/update")
    @ResponseBody
    public ResponseEntity<String> updateStudent(@RequestBody Map<String, Object> requestData) {
        try {
            // 从请求数据中获取原始手机号和新手机号
            String originalPhone = (String) requestData.get("originalPhone");
            String newPhone = (String) requestData.get("phone");
            String genderStr = (String) requestData.get("gender");
            String studentId = (String) requestData.get("studentId");
            System.out.println("studentid: " + studentId);
            String birthDateStr = (String) requestData.get("birthDate");

            // 检查原始学生是否存在
            SeekerDTO existingSeeker = seekerService.findByPhone(originalPhone);
            if (existingSeeker == null) {
                return ResponseEntity.badRequest().body("原始学生不存在，手机号: " + originalPhone);
            }

            // 如果手机号有变化，检查新手机号是否已存在
            if (!originalPhone.equals(newPhone)) {
                SeekerDTO seekerWithNewPhone = seekerService.findByPhone(newPhone);
                if (seekerWithNewPhone != null) {
                    return ResponseEntity.badRequest().body("新手机号已存在: " + newPhone);
                }
            }

            // 创建SeekerDTO对象
            SeekerDTO seekerDTO = new SeekerDTO();
            seekerDTO.setStudentId(studentId);
            seekerDTO.setName((String) requestData.get("name"));
            if (genderStr != null) {
                if ("男".equals(genderStr) || "MALE".equals(genderStr)) {
                    seekerDTO.setGender(Seeker.Gender.男);
                } else if ("女".equals(genderStr) || "FEMALE".equals(genderStr)) {
                    seekerDTO.setGender(Seeker.Gender.女);
                } else {
                    // 默认值或抛出异常
                    seekerDTO.setGender(Seeker.Gender.男); // 或根据需要设置默认值
                }
            }
            if (birthDateStr != null && !birthDateStr.isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date birthDate = sdf.parse(birthDateStr);
                    seekerDTO.setBirthDate(birthDate);
                } catch (ParseException e) {
                    // 如果日期解析失败，保持原有日期
                    seekerDTO.setBirthDate(existingSeeker.getBirthDate());
                }
            } else {
                // 如果没有提供新日期，保持原有日期
                seekerDTO.setBirthDate(existingSeeker.getBirthDate());
            }
            seekerDTO.setPhone(newPhone);
            seekerDTO.setPassword((String) requestData.get("password"));

            // 更新学生信息，使用原始手机号作为查找条件
            seekerService.update(originalPhone, seekerDTO);
            return ResponseEntity.ok("学生信息更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("更新失败: " + e.getMessage());
        }
    }

    // 删除学生接口
    @PostMapping("/seekerList/delete")
    @ResponseBody
    public ResponseEntity<String> deleteStudent(@RequestParam String phone) {
        try {
            // 直接使用service删除，不再需要先查找
            seekerService.delete(phone);
            return ResponseEntity.ok("学生删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("删除失败: " + e.getMessage());
        }
    }

    // 企业管理页面跳转
    @GetMapping("/company")
    public String companyManagement(@RequestParam(required = false) String company,Model model) {
         List<RecruiterDTO> recruiterList;

         if (company != null && !company.trim().isEmpty()) {
             // 如果提供了公司名筛选条件，则按公司名筛选
             recruiterList = recruiterService.getAllRecruiters().stream()
                     .filter(recruiter -> recruiter.getCompany() != null &&
                             recruiter.getCompany().contains(company))
                     .collect(Collectors.toList());
         } else {
             // 如果没有筛选条件，获取所有企业用户
             recruiterList = recruiterService.getAllRecruiters();
         }

         model.addAttribute("recruiterList", recruiterList);
         // 保留筛选条件以便在页面上显示
         model.addAttribute("company", company);
         return "company";
    }

    @GetMapping("/recruiterList/get")
    @ResponseBody
    public ResponseEntity<RecruiterDTO> getRecruiter(@RequestParam String phone) {
        try {
             RecruiterDTO recruiter = recruiterService.findByPhone(phone);
             if (recruiter != null) {
                 return ResponseEntity.ok(recruiter);
             } else {
                 return ResponseEntity.notFound().build();
             }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/recruiterList/add")
    @ResponseBody
    public ResponseEntity<String> addRecruiter(@RequestBody RecruiterDTO recruiterDTO) {
        try {
             recruiterService.createRecruiter(recruiterDTO);
             return ResponseEntity.ok("企业用户添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("添加失败: " + e.getMessage());
        }
    }

    @PostMapping("/recruiterList/update")
    @ResponseBody
    public ResponseEntity<String> updateRecruiter(@RequestBody Map<String, Object> requestData) {
        try {
            // 从请求数据中获取原始手机号和新手机号
            String originalPhone = (String) requestData.get("originalPhone");
            String newPhone = (String) requestData.get("phone");
            String genderStr = (String) requestData.get("gender");

            // 检查原始企业用户是否存在
            RecruiterDTO existingRecruiter = recruiterService.findByPhone(originalPhone);
            if (existingRecruiter == null) {
                return ResponseEntity.badRequest().body("原始企业用户不存在，手机号: " + originalPhone);
            }

            // 如果手机号有变化，检查新手机号是否已存在
            if (!originalPhone.equals(newPhone)) {
                RecruiterDTO recruiterWithNewPhone = recruiterService.findByPhone(newPhone);
                if (recruiterWithNewPhone != null) {
                    return ResponseEntity.badRequest().body("新手机号已存在: " + newPhone);
                }
            }

            // 创建RecruiterDTO对象
            RecruiterDTO recruiterDTO = new RecruiterDTO();
            recruiterDTO.setPhone(newPhone);
            recruiterDTO.setName((String) requestData.get("name"));
            recruiterDTO.setCompany((String) requestData.get("company"));
            recruiterDTO.setPosition((String) requestData.get("position"));
            recruiterDTO.setPassword((String) requestData.get("password"));

            // 处理性别字段
            if (genderStr != null) {
                if ("男".equals(genderStr)) {
                    recruiterDTO.setGender(Recruiter.Gender.男);
                } else if ("女".equals(genderStr)) {
                    recruiterDTO.setGender(Recruiter.Gender.女);
                } else {
                    recruiterDTO.setGender(Recruiter.Gender.男); // 默认值
                }
            }

            // 更新企业用户信息，使用原始手机号作为查找条件
            recruiterService.update(originalPhone, recruiterDTO);
            return ResponseEntity.ok("企业用户信息更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("更新失败: " + e.getMessage());
        }
    }

    @PostMapping("/recruiterList/delete")
    @ResponseBody
    public ResponseEntity<String> deleteRecruiter(@RequestParam String phone) {
        try {
            recruiterService.delete(phone);
            return ResponseEntity.ok("企业用户删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("删除失败: " + e.getMessage());
        }
    }

    // 岗位审核页面跳转
    @GetMapping("/position")
    public String positionApproval(Model model) {
        try {
            // 获取所有岗位数据
            List<JobPositionDTO> positions = jobPositionService.getAllJobPositions();
            model.addAttribute("positions", positions);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("positions", new ArrayList<JobPositionDTO>());
        }
        return "position";
    }

    @GetMapping("/position/list")
    @ResponseBody
    public ResponseEntity<List<JobPositionDTO>> listPositions(
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String positionName,
            @RequestParam(required = false) String status) {
        try {
            List<JobPositionDTO> positions = jobPositionService.getAllJobPositions();

            // 根据筛选条件过滤
            if (company != null && !company.isEmpty()) {
                positions = positions.stream()
                        .filter(p -> p.getCompanyName() != null && p.getCompanyName().contains(company))
                        .collect(Collectors.toList());
            }

            if (positionName != null && !positionName.isEmpty()) {
                positions = positions.stream()
                        .filter(p -> p.getTitle() != null && p.getTitle().contains(positionName))
                        .collect(Collectors.toList());
            }

            if (status != null && !status.isEmpty()) {
                positions = positions.stream()
                        .filter(p -> p.getStatus() != null && p.getStatus().toString().equals(status))
                        .collect(Collectors.toList());
            }

            return ResponseEntity.ok(positions);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }


    @GetMapping("/position/get")
    @ResponseBody
    public ResponseEntity<JobPositionDTO> getPositionById(@RequestParam int id) {
        try {
            JobPositionDTO position = jobPositionService.getJobPositionById(id);
            if (position != null) {
                return ResponseEntity.ok(position);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/position/update")
    @ResponseBody
    public ResponseEntity<String> updatePosition(@RequestBody JobPositionDTO positionDTO) {
        try {
            jobPositionService.updateJobPosition(positionDTO.getId(), positionDTO);
            return ResponseEntity.ok("岗位信息更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("更新失败: " + e.getMessage());
        }
    }

    @PostMapping("/position/delete")
    @ResponseBody
    public ResponseEntity<String> deletePosition(@RequestBody Map<String, Object> requestData) {
        try {
            Integer id = (Integer) requestData.get("id");
            if (id == null) {
                return ResponseEntity.badRequest().body("岗位ID不能为空");
            }

            jobPositionService.deleteJobPosition(id);
            return ResponseEntity.ok("岗位删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("删除失败: " + e.getMessage());
        }
    }

    //发布量Top10岗位数据接口（对应前端/publish-top10请求）
    @GetMapping("/position/stat/publish-top10")
    @ResponseBody
    public ResponseEntity<List<PositionStatDTO>> getPublishTop10() {
        try {
            List<PositionStatDTO> top10 = jobPositionService.getPublishTop10();
            return ResponseEntity.ok(top10);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    //投递量Top10岗位数据接口（对应前端/delivery-top10请求）
    @GetMapping("/position/stat/delivery-top10")
    @ResponseBody
    public ResponseEntity<List<PositionStatDTO>> getDeliveryTop10() {
        try {
            List<PositionStatDTO> top10 = jobPositionService.getDeliveryTop10();
            return ResponseEntity.ok(top10);
        } catch (Exception e) {
            e.printStackTrace(); // 查看具体错误原因
            return ResponseEntity.status(500).build();
        }
    }
    // 成绩录入页面跳转
    @GetMapping("/scores")
    public String scoreEntry(Model model) {
        try {
            // 预加载主成绩表数据
            List<AcademicRecordDTO> mainRecords = academicRecordService.getAllAcademicRecords();
            model.addAttribute("mainRecords", mainRecords);

            List<AcademicRecordDTO> tempRecords = tempAcademicRecordService.getAllTempAcademicRecords();
            model.addAttribute("tempRecords", tempRecords);

            List<SeekerDTO> students = seekerService.getAllSeekers();
            model.addAttribute("students", students);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("mainRecords", new ArrayList<AcademicRecordDTO>());
            model.addAttribute("tempRecords", new ArrayList<AcademicRecordDTO>());
            model.addAttribute("students", new ArrayList<SeekerDTO>());
        }

        return "scores";
    }

    @GetMapping("/academicRecord/list")
    @ResponseBody
    public ResponseEntity<List<AcademicRecordDTO>> listAcademicRecords(
            @RequestParam(required = false) String studentNumber,
            @RequestParam(required = false) String subjectName) {
        try {
            List<AcademicRecordDTO> records;
            if (studentNumber != null && !studentNumber.isEmpty()) {
                if (subjectName != null && !subjectName.isEmpty()) {
                    // 同时按学号和课程名筛选
                    records = academicRecordService.getAllAcademicRecords().stream()
                            .filter(r -> r.getStudentNumber().equals(studentNumber) &&
                                    r.getSubjectName().equals(subjectName))
                            .collect(Collectors.toList());
                } else {
                    // 只按学号筛选
                    records = academicRecordService.getAcademicRecordsByStudent(studentNumber);
                }
            } else if (subjectName != null && !subjectName.isEmpty()) {
                // 只按课程名筛选
                records = academicRecordService.getAllAcademicRecords().stream()
                        .filter(r -> r.getSubjectName().equals(subjectName))
                        .collect(Collectors.toList());
            } else {
                // 获取所有记录
                records = academicRecordService.getAllAcademicRecords();
            }
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/academicRecord/get")
    @ResponseBody
    public ResponseEntity<AcademicRecordDTO> getAcademicRecord(
            @RequestParam String studentNumber,
            @RequestParam String subjectName,
            @RequestParam String academicYear,
            @RequestParam String semester) {
        try {
            AcademicRecordDTO record = academicRecordService.getAcademicRecordById(
                    studentNumber, subjectName, academicYear, semester);
            if (record != null) {
                return ResponseEntity.ok(record);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/academicRecord/create")
    @ResponseBody
    public ResponseEntity<String> createAcademicRecord(@RequestBody AcademicRecordDTO recordDTO) {
        try {
            // 检查学生是否存在
            List<SeekerDTO> seekers = seekerService.getAllSeekers();
            boolean studentExists = seekers.stream()
                    .anyMatch(seeker -> seeker.getStudentId() != null &&
                            seeker.getStudentId().equals(recordDTO.getStudentNumber()));

            if (!studentExists) {
                return ResponseEntity.badRequest().body("学号不存在");
            }

            // 检查是否已存在相同记录
            AcademicRecordDTO existing = academicRecordService.getAcademicRecordById(
                    recordDTO.getStudentNumber(),
                    recordDTO.getSubjectName(),
                    recordDTO.getAcademicYear(),
                    recordDTO.getSemester());

            if (existing != null) {
                return ResponseEntity.badRequest().body("该成绩记录已存在");
            }

            academicRecordService.createAcademicRecord(recordDTO);
            return ResponseEntity.ok("成绩记录添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("添加失败: " + e.getMessage());
        }
    }

    @PostMapping("/academicRecord/update")
    @ResponseBody
    public ResponseEntity<String> updateAcademicRecord(
            @RequestParam String studentNumber,
            @RequestParam String subjectName,
            @RequestParam String academicYear,
            @RequestParam String semester,
            @RequestBody AcademicRecordDTO recordDTO) {
        try {
            AcademicRecordDTO updated = academicRecordService.updateAcademicRecord(
                    studentNumber, subjectName, academicYear, semester, recordDTO);
            if (updated != null) {
                return ResponseEntity.ok("成绩记录更新成功");
            } else {
                return ResponseEntity.badRequest().body("成绩记录不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("更新失败: " + e.getMessage());
        }
    }

    @PostMapping("/academicRecord/delete")
    @ResponseBody
    public ResponseEntity<String> deleteAcademicRecord(
            @RequestParam String studentNumber,
            @RequestParam String subjectName,
            @RequestParam String academicYear,
            @RequestParam String semester) {
        try {
            academicRecordService.deleteAcademicRecord(studentNumber, subjectName, academicYear, semester);
            return ResponseEntity.ok("成绩记录删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("删除失败: " + e.getMessage());
        }
    }

    @PostMapping("/academicRecord/importExcel")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> importExcelRecords(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "请选择文件");
                return ResponseEntity.badRequest().body(response);
            }

            String fileName = file.getOriginalFilename();
            if (fileName == null || (!fileName.toLowerCase().endsWith(".xls") && !fileName.toLowerCase().endsWith(".xlsx"))) {
                response.put("success", false);
                response.put("message", "请选择.xls或.xlsx格式的Excel文件");
                return ResponseEntity.badRequest().body(response);
            }

            List<AcademicRecordDTO> records = parseExcelFile(file);
            academicRecordService.importAcademicRecords(records);

            response.put("success", true);
            response.put("message", "数据导入临时表成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "导入失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // 解析Excel文件
    private List<AcademicRecordDTO> parseExcelFile(MultipartFile file) throws IOException {
        List<AcademicRecordDTO> records = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook;
            if (file.getOriginalFilename().toLowerCase().endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(inputStream);
            } else {
                workbook = new HSSFWorkbook(inputStream);
            }

            Sheet sheet = workbook.getSheetAt(0); // 获取第一个工作表

            // 从第二行开始读取数据（跳过标题行）
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                // 确保行中有足够的单元格
                if (row.getLastCellNum() < 6) continue;

                AcademicRecordDTO record = new AcademicRecordDTO();

                // 读取学号
                Cell studentNumberCell = row.getCell(0);
                if (studentNumberCell != null) {
                    record.setStudentNumber(getCellValueAsString(studentNumberCell));
                }

                // 读取课程名
                Cell subjectNameCell = row.getCell(1);
                if (subjectNameCell != null) {
                    record.setSubjectName(getCellValueAsString(subjectNameCell));
                }

                // 读取学年
                Cell academicYearCell = row.getCell(2);
                if (academicYearCell != null) {
                    record.setAcademicYear(getCellValueAsString(academicYearCell));
                }

                // 读取学期
                Cell semesterCell = row.getCell(3);
                if (semesterCell != null) {
                    record.setSemester(getCellValueAsString(semesterCell));
                }

                // 读取成绩
                Cell scoreCell = row.getCell(4);
                if (scoreCell != null) {
                    try {
                        record.setScore(new BigDecimal(getCellValueAsString(scoreCell)));
                    } catch (NumberFormatException e) {
                        record.setScore(BigDecimal.ZERO);
                    }
                } else {
                    record.setScore(BigDecimal.ZERO);
                }

                // 读取学分
                Cell creditCell = row.getCell(5);
                if (creditCell != null) {
                    try {
                        record.setCredit(new BigDecimal(getCellValueAsString(creditCell)));
                    } catch (NumberFormatException e) {
                        record.setCredit(BigDecimal.ZERO);
                    }
                } else {
                    record.setCredit(BigDecimal.ZERO);
                }

                // 只有当学号和课程名不为空时才添加记录
                if (record.getStudentNumber() != null && !record.getStudentNumber().trim().isEmpty() &&
                        record.getSubjectName() != null && !record.getSubjectName().trim().isEmpty()) {
                    records.add(record);
                }
            }
        } catch (Exception e) {
            throw new IOException("解析Excel文件失败: " + e.getMessage(), e);
        }

        return records;
    }

    // 获取单元格值作为字符串
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // 防止科学计数法显示
                    return new BigDecimal(cell.getNumericCellValue()).toPlainString();
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "";
        }
    }

    @PostMapping("/academicRecord/migrate")
    @ResponseBody
    public ResponseEntity<String> migrateAcademicRecords(@RequestBody List<AcademicRecordDTO> records) {
        try {
            List<SeekerDTO> seekers = seekerService.getAllSeekers();
            Set<String> validStudentNumbers = seekers.stream()
                    .map(SeekerDTO::getStudentId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            List<AcademicRecordDTO> createdRecords = new ArrayList<>();
            List<AcademicRecordDTO> updatedRecords = new ArrayList<>();

            for (AcademicRecordDTO record : records) {
                if (record.getScore() == null || record.getCredit() == null) {
                    System.out.println("跳过记录，score或credit为空: " + record.getStudentNumber() + ", " + record.getSubjectName());
                    continue; // 跳过无效记录
                }

                if (validStudentNumbers.contains(record.getStudentNumber())) {
                    // 检查是否已存在相同记录
                    AcademicRecordDTO existing = academicRecordService.getAcademicRecordById(
                            record.getStudentNumber(),
                            record.getSubjectName(),
                            record.getAcademicYear(),
                            record.getSemester());

                    if (existing == null) {
                        // 记录不存在，创建新记录
                        academicRecordService.createAcademicRecord(record);
                        createdRecords.add(record);
                        System.out.println("成功创建记录: " + record.getStudentNumber() + ", " + record.getSubjectName());
                    } else {
                        // 记录已存在，更新记录
                        AcademicRecordDTO updateDTO = new AcademicRecordDTO();
                        updateDTO.setScore(record.getScore());
                        updateDTO.setCredit(record.getCredit());

                        AcademicRecordDTO updated = academicRecordService.updateAcademicRecord(
                                record.getStudentNumber(),
                                record.getSubjectName(),
                                record.getAcademicYear(),
                                record.getSemester(),
                                updateDTO);

                        if (updated != null) {
                            updatedRecords.add(record);
                            System.out.println("成功更新记录: " + record.getStudentNumber() + ", " + record.getSubjectName());
                        } else {
                            System.out.println("更新记录失败: " + record.getStudentNumber() + ", " + record.getSubjectName());
                        }
                    }
                } else {
                    System.out.println("学生不存在，跳过: " + record.getStudentNumber());
                }
            }

            // 从临时表中删除已处理的记录（包括创建和更新的记录）
            for (AcademicRecordDTO record : createdRecords) {
                tempAcademicRecordService.deleteTempAcademicRecord(
                        record.getStudentNumber(),
                        record.getSubjectName(),
                        record.getAcademicYear(),
                        record.getSemester());
            }

            for (AcademicRecordDTO record : updatedRecords) {
                tempAcademicRecordService.deleteTempAcademicRecord(
                        record.getStudentNumber(),
                        record.getSubjectName(),
                        record.getAcademicYear(),
                        record.getSemester());
            }

            return ResponseEntity.ok("数据迁移完成。新增: " + createdRecords.size() + "条，更新: " + updatedRecords.size() + "条");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("迁移失败: " + e.getMessage());
        }
    }

    // 临时成绩表相关接口
    @GetMapping("/tempAcademicRecord/list")
    @ResponseBody
    public ResponseEntity<List<AcademicRecordDTO>> listTempAcademicRecords() {
        try {
            List<AcademicRecordDTO> records = tempAcademicRecordService.getAllTempAcademicRecords();
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/tempAcademicRecord/delete")
    @ResponseBody
    public ResponseEntity<String> deleteTempAcademicRecord(
            @RequestParam String studentNumber,
            @RequestParam String subjectName,
            @RequestParam String academicYear,
            @RequestParam String semester) {
        try {
            tempAcademicRecordService.deleteTempAcademicRecord(studentNumber, subjectName, academicYear, semester);
            return ResponseEntity.ok("临时记录删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("删除失败: " + e.getMessage());
        }
    }

    @GetMapping("/seekerList/exists")
    @ResponseBody
    public ResponseEntity<Boolean> checkStudentExists(@RequestParam String studentId) {
        try {
            List<SeekerDTO> seekers = seekerService.getAllSeekers();
            boolean exists = seekers.stream()
                    .anyMatch(seeker -> seeker.getStudentId() != null &&
                            seeker.getStudentId().equals(studentId));
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(false);
        }
    }

    //管理员页面跳转
    @GetMapping("/admin")
    public String adminManagement(Model model) {
        List<Admin> adminList = adminService.getAllAdmins();
        model.addAttribute("adminList", adminList);
        return "admin";
    }

    @PostMapping("/adminList/add")
    @ResponseBody
    public ResponseEntity<String> addAdmin(@RequestBody Admin admin) {
        try {
            // 检查用户名是否已存在
            if (adminService.existsByUsername(admin.getUsername())) {
                return ResponseEntity.badRequest().body("用户名已存在");
            }
            if (admin.getPassword() == null || admin.getPassword().isEmpty()) {
                admin.setPassword("abc123"); // 设置默认密码
            }
            // 调用服务保存管理员信息
            adminService.createAdmin(admin);
            return ResponseEntity.ok("管理员添加成功");
        } catch (Exception e) {
            e.printStackTrace(); // 打印错误堆栈以便调试
            return ResponseEntity.status(500).body("添加失败: " + e.getMessage());
        }
    }

    // 获取管理员信息接口
    @GetMapping("/adminList/get")
    @ResponseBody
    public ResponseEntity<Admin> getAdmin(@RequestParam Integer id) {
        try {
            Admin admin = adminService.findById(id);
            if (admin != null) {
                return ResponseEntity.ok(admin);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    // 更新管理员信息接口
    @PostMapping("/adminList/update")
    @ResponseBody
    public ResponseEntity<String> updateAdmin(@RequestBody Admin admin) {
        try {
            // 添加ID空值检查
            if (admin.getAdminId() == null) {
                return ResponseEntity.badRequest().body("管理员ID不能为空");
            }

            // 检查原始管理员是否存在
            Admin existingAdmin = adminService.findById(admin.getAdminId());
            if (existingAdmin == null) {
                return ResponseEntity.badRequest().body("原始管理员不存在，ID: " + admin.getAdminId());
            }

            // 更新管理员信息
            adminService.update(admin);
            return ResponseEntity.ok("管理员信息更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("更新失败: " + e.getMessage());
        }
    }

    // 删除管理员接口
    @PostMapping("/adminList/delete")
    @ResponseBody
    public ResponseEntity<String> deleteAdmin(@RequestParam Integer id) {
        try {
            adminService.delete(id);
            return ResponseEntity.ok("管理员删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("删除失败: " + e.getMessage());
        }
    }

    // 个人信息页面跳转
    @GetMapping("/profile")
    public String userProfile(Model model) {
        // 获取当前登录用户信息
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            Admin admin = adminService.findByUsername(username);
            model.addAttribute("admin", admin);
        }
        return "profile";
    }

    @PostMapping("/profile/changePassword")
    @ResponseBody
    public ResponseEntity<String> changePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmNewPassword) {
        try {
            // 获取当前登录用户
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (!(principal instanceof UserDetails)) {
                return ResponseEntity.status(401).body("未登录");
            }

            String username = ((UserDetails) principal).getUsername();
            Admin admin = adminService.findByUsername(username);

            if (admin == null) {
                return ResponseEntity.badRequest().body("用户不存在");
            }

            // 验证原密码
            if (!admin.getPassword().equals(oldPassword)) {
                return ResponseEntity.badRequest().body("原密码错误");
            }

            // 验证新密码和确认密码是否一致
            if (!newPassword.equals(confirmNewPassword)) {
                return ResponseEntity.badRequest().body("新密码和确认密码不一致");
            }

            // 验证新密码格式
            if (!isValidPassword(newPassword)) {
                return ResponseEntity.badRequest().body("新密码必须由大写字母、小写字母和数字组成，长度为6-12位");
            }

            // 更新密码
            admin.setPassword(newPassword);
            adminService.update(admin);

            return ResponseEntity.ok("密码修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("密码修改失败: " + e.getMessage());
        }
    }

    // 验证密码格式的方法
    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 6 || password.length() > 12) {
            return false;
        }

        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpper = true;
            } else if (Character.isLowerCase(c)) {
                hasLower = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            }
        }

        return hasUpper && hasLower && hasDigit;
    }


    // 根路径默认跳转到首页
    @GetMapping("/")
    public String root() {
        return "redirect:/home";
    }
}
