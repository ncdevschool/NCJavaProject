package com.netcracker.devschool.dev4.studdist.service;

import com.netcracker.devschool.dev4.studdist.entity.HeadOfPractice;
import com.netcracker.devschool.dev4.studdist.repository.HeadOfPracticeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class HeadOfPracticeServiceImpl implements HeadOfPracticeService {

    @Resource
    private HeadOfPracticeRepository headOfPracticeRepository;


    @Override
    @Transactional
    public HeadOfPractice create(HeadOfPractice headOfPractice) {
        return headOfPracticeRepository.save(headOfPractice);
    }

    @Override
    @Transactional
    public HeadOfPractice delete(int id) throws Exception {
        HeadOfPractice deleted = headOfPracticeRepository.findOne(id);
        if (deleted == null) throw new Exception("Not found");
        headOfPracticeRepository.delete(id);
        return deleted;
    }

    @Override
    @Transactional
    public List<HeadOfPractice> findAll() {
        return headOfPracticeRepository.findAll();
    }

    @Override
    @Transactional
    public HeadOfPractice update(HeadOfPractice headOfPractice) throws Exception {
        return null; //TODO
    }

    @Override
    @Transactional
    public HeadOfPractice findById(int id) {
        return headOfPracticeRepository.findOne(id);
    }

    @Override
    public Page<HeadOfPractice> findForPractice(String searchKey, String sortBy, String order, int start, int length) {
        PageRequest pageR = new PageRequest(start / length, length, Sort.Direction.fromString(order), sortBy);
        return headOfPracticeRepository.findForTable(searchKey, pageR);
    }
}
