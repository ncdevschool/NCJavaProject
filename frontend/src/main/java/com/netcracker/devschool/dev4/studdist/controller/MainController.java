/*
 This software is the confidential information and copyrighted work of
 NetCracker Technology Corp. ("NetCracker") and/or its suppliers and
 is only distributed under the terms of a separate license agreement
 with NetCracker.
 Use of the software is governed by the terms of the license agreement.
 Any use of this software not in accordance with the license agreement
 is expressly prohibited by law, and may result in severe civil
 and criminal penalties. 
 
 Copyright (c) 1995-2017 NetCracker Technology Corp.
 
 All Rights Reserved.
 
*/
/*
 * Copyright 1995-2017 by NetCracker Technology Corp.,
 * University Office Park III
 * 95 Sawyer Road
 * Waltham, MA 02453
 * United States of America
 * All rights reserved.
 */
package com.netcracker.devschool.dev4.studdist.controller;

import com.netcracker.devschool.dev4.studdist.converters.HopsConverter;
import com.netcracker.devschool.dev4.studdist.entity.*;
import com.netcracker.devschool.dev4.studdist.form.HeadOfPracticeForm;
import com.netcracker.devschool.dev4.studdist.service.*;
import com.netcracker.devschool.dev4.studdist.utils.TableData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Random;

/**
 * @author anpi0316
 * Date: 06.10.2017
 * Time: 14:04
 */
@Controller
public class MainController {

    @Autowired
    private UserService userService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private HeadOfPracticeService headOfPracticeService;

    @Autowired
    private FacultyService facultyService;

    @Autowired
    private SpecialityService specialityService;

    @Autowired
    private HopsConverter hopsConverter;

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String registerPage() {

        return "register";

    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login(@RequestParam(value = "error", required = false) String error,
                              @RequestParam(value = "logout", required = false) String logout) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!(auth instanceof AnonymousAuthenticationToken)) {

    /* The user is logged in :) */
            String role = auth.getAuthorities().toString();


            String targetUrl = "";
            if (role.contains("STUDENT")) {
                targetUrl = "/student";
            } else if (role.contains("HOP")) {
                targetUrl = "/hop";
            } else if (role.contains("ADMIN")) {
                targetUrl = "/admin";
            }

            return new ModelAndView("redirect:" + targetUrl);

        }

        ModelAndView model = new ModelAndView();
        if (error != null) {
            model.addObject("error", "Invalid username and password!");
        }

        if (logout != null) {
            model.addObject("msg", "You've been logged out successfully.");
        }
        model.setViewName("login");

        return model;

    }

    @RequestMapping(value = "/student", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ModelAndView pageStudent() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        ModelAndView model = new ModelAndView();
        String name = auth.getName();
        int id = userService.getIdByName(name);
        Student student = studentService.findById(id);
        if (student != null) {
            model.addObject("name", student.getFname() + " " + student.getLname());
            model.addObject("imageUrl", "images/" + student.getImageUrl());
            model.addObject("id", student.getId());
            model.addObject("faculties", facultyService.findAll());
        }
        model.setViewName("student");
        return model;
    }

    @RequestMapping(value = "/headofpractice", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_HOP')")
    public ModelAndView pageHop() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        ModelAndView model = new ModelAndView();
        String name = auth.getName();
        int id = userService.getIdByName(name);
        HeadOfPractice headOfPractice = headOfPracticeService.findById(id);
        if (headOfPractice != null) {
            model.addObject("name", headOfPractice.getFname() + " " + headOfPractice.getLname());
            model.addObject("imageUrl", "images/" + headOfPractice.getImageUrl());
            model.addObject("company", headOfPractice.getCompanyName());
            model.addObject("id", id);
            model.addObject("faculties", facultyService.findAll());
        }
        model.setViewName("headofpractice");
        return model;
    }

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView pageAdmin() {
        ModelAndView model = new ModelAndView();
        model.addObject("faculties", facultyService.findAll());
        model.addObject("specialities", specialityService.findAll());
        List<Faculty> list = facultyService.findAll();
        model.setViewName("admin");
        return model;
    }

    @RequestMapping(value = "/admin/createRandom", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<Student> createStudents() {
        final String[] fnamesM = {"Александр", "Андрей", "Алексей", "Борис", "Владимир", "Владислав", "Валентин",
                "Георгий", "Геннадий", "Дмитрий", "Егор", "Иван", "Константин", "Кирилл", "Леонид",
                "Максим", "Никита", "Николай", "Олег", "Пётр", "Руслан", "Сергей", "Степан",
                "Тимур", "Фёдор"};
        final String[] fnamesW = {"Александра", "Алина", "Анна", "Анастасия", "Валерия", "Ваврвара", "Вероника",
                "Галина", "Дарья", "Елена", "Елизавета", "Ирина", "Карина", "Ксения", "Маргарита", "Наталья",
                "Оксана", "Ольга", "Полина", "Светлана", "София", "Татьяна", "Юлия"};
        final String[] lnamesM = {"Зинкевич", "Филипенко", "Скворцов", "Полищук", "Полещук", "Гончарук", "Высоцкий",
                "Пилипенко", "Зенкевич", "Лукашевич", "Корбут", "Любич", "Ярмолович", "Лозинский", "Басинский",
                "Голубовский", "Захаревич", "Смоляк", "Владомирский", "Янукович", "Ивашкевич", "Ананич", "Хруцкий",
                "Санько", "Виленский", "Мошковский", "Балицкий", "Мисюк", "Янушкевич", "Кондратович"};
        final String[] lnamesW = {"Зинкевич", "Филипенко", "Скворцова", "Полищук", "Полещук", "Гончарук", "Высоцкая",
                "Пилипенко", "Зенкевич", "Лукашевич", "Корбут", "Любич", "Ярмолович", "Лозинская", "Басинская",
                "Голубовская", "Захаревич", "Смоляк", "Владомирская", "Янукович", "Ивашкевич", "Ананич", "Хруцкая",
                "Санько", "Виленская", "Мошковская", "Балицкая", "Мисюк", "Янушкевич", "Кондратович"};
        for (int i = 0; i < 5000; i++) {
            Random random = new Random();
            int sex = random.nextInt(2);
            String fname;
            String lname;
            if (sex == 1) {
                fname = fnamesM[random.nextInt(25)];
                lname = lnamesM[random.nextInt(30)];
            } else {
                fname = fnamesW[random.nextInt(23)];
                lname = lnamesW[random.nextInt(30)];
            }
            int speciality = random.nextInt(36) + 1;
            int faculty = specialityService.findById(speciality).getFacultyId();
            int group = (random.nextInt(7) + 1) * 100000 + faculty * 10000 + speciality * 100 + random.nextInt(3);
            int isBudget = random.nextInt(2);
            double avg = random.nextDouble() * 6 + 4;

            String username = "student" + (i) + "@bsuir.by";

            User user = new User();
            user.setUsername(username);
            String password = org.apache.commons.codec.digest.DigestUtils.sha256Hex("123456");
            user.setPassword(password);
            user.setEnabled(1);
            UserRoles userRoles = new UserRoles();
            userRoles.setUsername(username);
            userRoles.setRole("ROLE_STUDENT");
            int id = userService.create(user, userRoles).getUser_role_id();
            Student student = new Student();
            student.setId(id);
            student.setFname(fname);
            student.setLname(lname);
            student.setImageUrl("student_default_avatar.png");
            student.setGroup(group);
            student.setAvgScore(avg);
            student.setFacultyId(faculty);
            student.setSpecialityId(speciality);
            student.setIsBudget(isBudget);
            studentService.create(student);
        }

        return studentService.findAll();
    }

    //for 403 access denied page
    @RequestMapping(value = "/403", method = RequestMethod.GET)
    public String accesssDenied() {

        return "403";

    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login?logout";//You can redirect wherever you want, but generally it's a good practice to show login screen again.
    }

    @RequestMapping(value = "errors", method = RequestMethod.GET)
    public String renderErrorPage(HttpServletRequest httpRequest) {

        String error = "";
        int httpErrorCode = getErrorCode(httpRequest);

        switch (httpErrorCode) {
            case 404: {
                error = "404";
                break;
            }
            case 500: {
                error = "500";
                break;
            }
        }
        return error;
    }

    private int getErrorCode(HttpServletRequest httpRequest) {
        return (Integer) httpRequest
                .getAttribute("javax.servlet.error.status_code");
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ModelAndView registerStudent(@RequestParam(value = "username") String username,
                                        @RequestParam(value = "password") String password) {
        User user = new User();
        user.setUsername(username);
        password = org.apache.commons.codec.digest.DigestUtils.sha256Hex(password);
        user.setPassword(password);
        user.setEnabled(1);
        UserRoles userRoles = new UserRoles();
        userRoles.setUsername(username);
        userRoles.setRole("ROLE_STUDENT");
        int id = userService.create(user, userRoles).getUser_role_id();
        Student student = new Student();
        student.setId(id);
        student.setFname("");
        student.setLname("");
        student.setImageUrl("student_default_avatar.png");
        student.setGroup(100000);
        student.setAvgScore(10);
        student.setFacultyId(1);
        student.setSpecialityId(1);
        studentService.create(student);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("msg", "Вы были успешно зарегистрированы. Теперь вы можете войти с ипользованием указанного логина и пароля");
        modelAndView.setViewName("login");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/newStudent", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseBody
    public Object newStudent(@RequestParam(value = "username") String username,
                             @RequestParam(value = "password") String password) {
        User user = new User();
        user.setUsername(username);
        password = org.apache.commons.codec.digest.DigestUtils.sha256Hex(password);
        user.setPassword(password);
        user.setEnabled(1);
        UserRoles userRoles = new UserRoles();
        userRoles.setUsername(username);
        userRoles.setRole("ROLE_STUDENT");
        int id = userService.create(user, userRoles).getUser_role_id();
        Student student = new Student();
        student.setId(id);
        student.setFname("");
        student.setLname("");
        student.setImageUrl("student_default_avatar.png");
        student.setGroup(100000);
        student.setAvgScore(10);
        student.setFacultyId(1);
        student.setSpecialityId(1);
        studentService.create(student);
        return id;
    }

    @RequestMapping(value = "/admin/createHop", method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Object createHOP(@Valid @ModelAttribute("headOfPracticeForm") HeadOfPracticeForm headOfPracticeForm, BindingResult result) {
        if (result.hasErrors()) {
            return result.getAllErrors();
        } else {
            User user = new User();
            user.setUsername(headOfPracticeForm.getEmail());
            String password = org.apache.commons.codec.digest.DigestUtils.sha256Hex(headOfPracticeForm.getPassword());
            user.setPassword(password);
            user.setEnabled(1);
            UserRoles userRoles = new UserRoles();
            userRoles.setUsername(headOfPracticeForm.getEmail());
            userRoles.setRole("ROLE_HOP");
            int id = userService.create(user, userRoles).getUser_role_id();
            HeadOfPractice headOfPractice = new HeadOfPractice();
            headOfPractice.setId(id);
            headOfPractice.setCompanyName(headOfPracticeForm.getCompanyName());
            headOfPractice.setFname(headOfPracticeForm.getFname());
            headOfPractice.setLname(headOfPracticeForm.getLname());
            headOfPractice.setImageUrl("hop_default_avatar.png");
            return headOfPracticeService.create(headOfPractice);
        }
    }

    @RequestMapping(value = "/admin/tableHop", method = RequestMethod.GET)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    private TableData returnTable(
            @RequestParam(value = "start") String start,
            @RequestParam(value = "length") String length,
            @RequestParam(value = "draw") String draw,
            @RequestParam(value = "search[value]", required = false) String key,
            @RequestParam(value = "order[0][column]") String order,
            @RequestParam(value = "order[0][dir]") String orderDir) {
        if (key == null) key = "";
        TableData result = new TableData();
        String[] columns = {"fname", "lname", "companyName", "id"};
        int i = Integer.parseInt(order) - 1;
        if (i < 0 || i > 2) i = 3;
        Page<HeadOfPractice> page = headOfPracticeService.findForPractice(key, columns[i], orderDir, Integer.parseInt(start), Integer.parseInt(length));
        List<HeadOfPractice> list = page.getContent();
        result.setRecordsTotal((int) page.getTotalElements() - page.getNumberOfElements());
        result.setRecordsFiltered((int) page.getTotalElements() - page.getNumberOfElements());
        result.setDraw(Integer.parseInt(draw));
        for (HeadOfPractice hop : list)
            result.addData(hopsConverter.hopToStringArray(hop));
        return result;
    }

}
/*
 WITHOUT LIMITING THE FOREGOING, COPYING, REPRODUCTION, REDISTRIBUTION,
 REVERSE ENGINEERING, DISASSEMBLY, DECOMPILATION OR MODIFICATION
 OF THE SOFTWARE IS EXPRESSLY PROHIBITED, UNLESS SUCH COPYING,
 REPRODUCTION, REDISTRIBUTION, REVERSE ENGINEERING, DISASSEMBLY,
 DECOMPILATION OR MODIFICATION IS EXPRESSLY PERMITTED BY THE LICENSE
 AGREEMENT WITH NETCRACKER. 
 
 THIS SOFTWARE IS WARRANTED, IF AT ALL, ONLY AS EXPRESSLY PROVIDED IN
 THE TERMS OF THE LICENSE AGREEMENT, EXCEPT AS WARRANTED IN THE
 LICENSE AGREEMENT, NETCRACKER HEREBY DISCLAIMS ALL WARRANTIES AND
 CONDITIONS WITH REGARD TO THE SOFTWARE, WHETHER EXPRESS, IMPLIED
 OR STATUTORY, INCLUDING WITHOUT LIMITATION ALL WARRANTIES AND
 CONDITIONS OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 TITLE AND NON-INFRINGEMENT.
 
 Copyright (c) 1995-2017 NetCracker Technology Corp.
 
 All Rights Reserved.
*/