package com.prodapt.learningspring.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.prodapt.learningspring.model.Classroom;
import com.prodapt.learningspring.model.Student;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/classroom")
public class ClassroomController {

  @Autowired
  private Classroom classroom;

  @GetMapping
  public String listAllStudents(Model model) {
    if (!model.containsAttribute("student"))
      model.addAttribute("student", new Student());
    model.addAttribute("students", classroom.getStudents());
    return "classroom";
  }

  @PostMapping("/add")
  public String addStudent(@Valid Student student, BindingResult bindingResult, RedirectAttributes attr) {
    if (bindingResult.hasErrors()) {
      attr.addFlashAttribute("org.springframework.validation.BindingResult.student", bindingResult);
      attr.addFlashAttribute("student", student);
      return "redirect:/classroom";
      
    }
    classroom.add(student);
    createStudent(student);
    return "redirect:/classroom";
  }

  @PostMapping("/delete")
  public String deleteStudent(int idx) {
    classroom.remove(idx);
    return "redirect:/classroom";
  }
  /*
   * public String deleteStudent(HttpServletRequest req) {
    classroom.remove(Integer.valueOf(req.getParameter("idx")));
    return "redirect:/classroom";
  }
   */

  @GetMapping("/edit")
  public String editStudentForm(@RequestParam int id, Model model) {
    Student student;
    if (!model.containsAttribute("student")) {
      student = classroom.getById(id).get();
      model.addAttribute("student", student);
    }
    return "studentEditForm";
  }

  @PostMapping("/edit")
  public String editStudent(@Valid Student student,BindingResult bindingResult, RedirectAttributes attr) {
    if (bindingResult.hasErrors()) {
      attr.addFlashAttribute("org.springframework.validation.BindingResult.student", bindingResult);
      attr.addFlashAttribute("student", student);
      attr.addAttribute("id", student.getId());
      return "redirect:/classroom/edit";
    }
    else {
      classroom.replace(student.getId(), student);
      return "redirect:/classroom";
    }
  }
  
  public void createStudent(Student student) {
  	try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
  	try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/student", "Ashish", "welcome")) {
      	System.out.println("helo");
          String sql = "INSERT INTO students (id, name, score) VALUES (?, ?, ?)";
          try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
          	System.out.println("heloww");
              preparedStatement.setInt(1, student.getId());
              preparedStatement.setString(2, student.getName());
              preparedStatement.setInt(3, student.getScore());
              preparedStatement.executeUpdate();
          }
      } catch (SQLException e) {
          e.printStackTrace(); 
      }
  }


}