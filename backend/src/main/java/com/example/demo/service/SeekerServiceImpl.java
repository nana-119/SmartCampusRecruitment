package com.example.demo.service;

import com.example.demo.dto.SeekerDTO;
import com.example.demo.model.AcademicRecord;
import com.example.demo.model.AcademicRecordId;
import com.example.demo.model.Seeker;
import com.example.demo.model.TempAcademicRecord;
import com.example.demo.repository.AcademicRecordRepository;
import com.example.demo.repository.SeekerRepository;
import com.example.demo.repository.TempAcademicRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SeekerServiceImpl implements SeekerService{

    private SeekerRepository seekerRepository;
    private TempAcademicRecordRepository tempAcademicRecordRepository;
    private AcademicRecordRepository academicRecordRepository;
    @Autowired
    public SeekerServiceImpl(SeekerRepository seekerRepository, TempAcademicRecordRepository tempAcademicRecordRepository, AcademicRecordRepository academicRecordRepository) {

        this.seekerRepository = seekerRepository;
        this.tempAcademicRecordRepository = tempAcademicRecordRepository;
        this.academicRecordRepository = academicRecordRepository;
    }


    @Override
    @Transactional
    public SeekerDTO createSeeker(SeekerDTO seekerDTO) {
        Seeker seeker = new Seeker();
        seeker.setPhone(seekerDTO.getPhone());
        seeker.setPassword(seekerDTO.getPassword());
        seeker.setName(seekerDTO.getName());
        seeker.setGender(seekerDTO.getGender());
        seeker.setBirthDate(seekerDTO.getBirthDate());
        seeker.setStudentId(seekerDTO.getStudentId());
        seeker.setHobbies(seekerDTO.getHobbies());
        if (seekerDTO.getResumeFile() != null) {
            try {
                seeker.setResumeFile(Base64.getDecoder().decode(seekerDTO.getResumeFile()));
            } catch (IllegalArgumentException e) {
                // 尝试使用URL安全Base64解码器
                seeker.setResumeFile(Base64.getUrlDecoder().decode(seekerDTO.getResumeFile()));
            }
        }
        if (seekerDTO.getTouxiang() != null) {
            try {
                seeker.setTouxiang(Base64.getDecoder().decode(seekerDTO.getTouxiang()));
            } catch (IllegalArgumentException e) {
                // 尝试使用URL安全Base64解码器
                seeker.setTouxiang(Base64.getUrlDecoder().decode(seekerDTO.getTouxiang()));
            }
        }
        Seeker savedSeeker = seekerRepository.save(seeker);
        return convertToDTO(savedSeeker);
    }

    @Override
    @Transactional
    public List<SeekerDTO> getAllSeekers() {
        return seekerRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SeekerDTO findByPhone(String phone) {
        return seekerRepository.findByPhone(phone)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Override
    @Transactional
    public SeekerDTO update(String phone, SeekerDTO seekerDTO) {
        Seeker seeker = seekerRepository.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        System.out.println("12geg   "+seekerDTO.getStudentId());

        seeker.setName(seekerDTO.getName());
        seeker.setPhone(seekerDTO.getPhone());
        seeker.setPassword(seekerDTO.getPassword());
        seeker.setGender(seekerDTO.getGender());
        seeker.setBirthDate(seekerDTO.getBirthDate());
        seeker.setHobbies(seekerDTO.getHobbies());
        if (seekerDTO.getResumeFile() != null) {
            try {
                seeker.setResumeFile(Base64.getDecoder().decode(seekerDTO.getResumeFile()));
            } catch (IllegalArgumentException e) {
                // 尝试使用URL安全Base64解码器
                seeker.setResumeFile(Base64.getUrlDecoder().decode(seekerDTO.getResumeFile()));
            }
        }
        if (seekerDTO.getTouxiang() != null) {
            try {
                seeker.setTouxiang(Base64.getDecoder().decode(seekerDTO.getTouxiang()));
            } catch (IllegalArgumentException e) {
                // 尝试使用URL安全Base64解码器
                seeker.setTouxiang(Base64.getUrlDecoder().decode(seekerDTO.getTouxiang()));
            }
        }
        if (seekerDTO.getTouxiang() != null) {
            try {
                seeker.setTouxiang(Base64.getDecoder().decode(seekerDTO.getTouxiang()));
            } catch (IllegalArgumentException e) {
                seeker.setTouxiang(Base64.getUrlDecoder().decode(seekerDTO.getTouxiang()));
            }
        }

        if (seekerDTO.getStudentId() != null) {
            List<TempAcademicRecord> tempRecords = tempAcademicRecordRepository.findByStudentNumber(seekerDTO.getStudentId());
            for (TempAcademicRecord tempRecord : tempRecords) {
                AcademicRecordId id = new AcademicRecordId(
                        tempRecord.getStudentNumber(),
                        tempRecord.getSubjectName(),
                        tempRecord.getAcademicYear(),
                        tempRecord.getSemester()
                );

                Optional<AcademicRecord> existingRecord = academicRecordRepository.findById(id);
                if (existingRecord.isPresent()) {
                    // 如果记录已存在，更新现有记录
                    AcademicRecord record = existingRecord.get();
                    record.setScore(tempRecord.getScore());
                    record.setCredit(tempRecord.getCredit());
                    academicRecordRepository.save(record);
                } else {
                    // 如果记录不存在，插入新记录
                    AcademicRecord record = new AcademicRecord();
                    record.setStudentNumber(tempRecord.getStudentNumber());
                    record.setSubjectName(tempRecord.getSubjectName());
                    record.setScore(tempRecord.getScore());
                    record.setCredit(tempRecord.getCredit());
                    record.setAcademicYear(tempRecord.getAcademicYear());
                    record.setSemester(tempRecord.getSemester());
                    academicRecordRepository.save(record);
                }
            }
            // 删除临时表中的已迁移数据
            tempAcademicRecordRepository.deleteAll(tempRecords);
        }

        Seeker updatedSeeker = seekerRepository.save(seeker);
        return convertToDTO(updatedSeeker);
    }

    @Override
    @Transactional
    public void delete(String phone) {
        Seeker seeker = seekerRepository.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        seekerRepository.delete(seeker);
    }

    @Override
    public SeekerDTO updateResumeByPhone(String phone, String resumeFile) {
        Seeker seeker = seekerRepository.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (resumeFile != null) {
            try {
                seeker.setResumeFile(Base64.getDecoder().decode(resumeFile));
            } catch (IllegalArgumentException e) {
                seeker.setResumeFile(Base64.getUrlDecoder().decode(resumeFile));
            }
        }

        Seeker updatedSeeker = seekerRepository.save(seeker);
        return convertToDTO(updatedSeeker);
    }

    private SeekerDTO convertToDTO(Seeker seeker) {
        SeekerDTO seekerDTO = new SeekerDTO();
        seekerDTO.setPhone(seeker.getPhone());
        seekerDTO.setPassword(seeker.getPassword());
        seekerDTO.setName(seeker.getName());
        seekerDTO.setGender(seeker.getGender());
        seekerDTO.setBirthDate(seeker.getBirthDate());
        seekerDTO.setStudentId(seeker.getStudentId());
        seekerDTO.setHobbies(seeker.getHobbies());
        seekerDTO.setResumeFile(encodeByteArrayToBase64(seeker.getResumeFile()));
        seekerDTO.setTouxiang(encodeByteArrayToBase64(seeker.getTouxiang()));
        return seekerDTO;
    }
    private String encodeByteArrayToBase64(byte[] data) {
        if (data == null) {
            return null; // 或者返回空字符串 ""
        }
        return Base64.getEncoder().encodeToString(data);
    }
}
