package com.bbs.servlet;

import com.bbs.entity.User;
import com.bbs.service.UserService;
import com.bbs.service.PostService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/user/*")
public class UserServlet extends HttpServlet {
    private final UserService userService = new UserService();
    private final PostService postService = new PostService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        resp.setContentType("application/json;charset=UTF-8");

        if ("/register".equals(path)) {
            String username = req.getParameter("username");
            String password = req.getParameter("password");
            String nickname = req.getParameter("nickname");
            String email = req.getParameter("email");
            // 如果没有提供昵称，默认使用用户名
            if (nickname == null || nickname.trim().isEmpty()) {
                nickname = username;
            }
            System.out.println("[DEBUG] /register - username: " + username + ", password length: " + (password != null ? password.length() : 0));

            if (username == null || password == null) {
                resp.getWriter().write("{\"success\":false,\"message\":\"用户名和密码不能为空\"}");
                return;
            }
            User user = userService.register(username, password, nickname, email);
            if (user != null) {
                resp.getWriter().write("{\"success\":true,\"message\":\"注册成功\"}");
            } else {
                resp.getWriter().write("{\"success\":false,\"message\":\"用户名已存在\"}");
            }
        } else if ("/login".equals(path)) {
            String username = req.getParameter("username");
            String password = req.getParameter("password");
            User user = userService.login(username, password);
            if (user != null) {
                HttpSession session = req.getSession();
                session.setAttribute("user", user);
                resp.getWriter().write("{\"success\":true,\"message\":\"登录成功\",\"user\":{\"id\":" + user.getId() + ",\"username\":\"" + user.getUsername() + "\",\"role\":" + user.getRole() + "}}");
            } else {
                resp.getWriter().write("{\"success\":false,\"message\":\"用户名或密码错误\"}");
            }
        } else if ("/logout".equals(path)) {
            req.getSession().invalidate();
            resp.getWriter().write("{\"success\":true,\"message\":\"已退出\"}");
        } else if ("/update".equals(path)) {
            HttpSession session = req.getSession();
            User loginUser = (User) session.getAttribute("user");
            if (loginUser == null) {
                resp.getWriter().write("{\"success\":false,\"message\":\"请先登录\"}");
                return;
            }
            loginUser.setNickname(req.getParameter("nickname"));
            loginUser.setEmail(req.getParameter("email"));
            loginUser.setPhone(req.getParameter("phone"));
            loginUser.setContactType(req.getParameter("contactType"));
            loginUser.setWorkNature(req.getParameter("workNature"));
            loginUser.setWorkLocation(req.getParameter("workLocation"));
            if (userService.updateProfile(loginUser)) {
                session.setAttribute("user", userService.getById(loginUser.getId()));
                resp.getWriter().write("{\"success\":true,\"message\":\"更新成功\"}");
            } else {
                resp.getWriter().write("{\"success\":false,\"message\":\"更新失败\"}");
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        resp.setContentType("application/json;charset=UTF-8");
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");

        if ("/info".equals(path)) {
            if (user == null) {
                resp.getWriter().write("{\"success\":false,\"message\":\"未登录\"}");
            } else {
                User freshUser = userService.getById(user.getId());
                String json = String.format("{\"success\":true,\"user\":{\"id\":%d,\"username\":\"%s\",\"nickname\":\"%s\",\"email\":\"%s\",\"phone\":\"%s\",\"contactType\":\"%s\",\"workNature\":\"%s\",\"workLocation\":\"%s\",\"points\":%d,\"frozenPoints\":%d}}",
                    freshUser.getId(), freshUser.getUsername(), freshUser.getNickname() != null ? freshUser.getNickname() : "",
                    freshUser.getEmail() != null ? freshUser.getEmail() : "", freshUser.getPhone() != null ? freshUser.getPhone() : "",
                    freshUser.getContactType() != null ? freshUser.getContactType() : "", freshUser.getWorkNature() != null ? freshUser.getWorkNature() : "",
                    freshUser.getWorkLocation() != null ? freshUser.getWorkLocation() : "", freshUser.getPoints(), freshUser.getFrozenPoints() != null ? freshUser.getFrozenPoints() : 0);
                resp.getWriter().write(json);
            }
        } else if ("/stats".equals(path)) {
            int userCount = userService.getUserCount();
            int postCount = postService.getPostCount();
            resp.getWriter().write("{\"success\":true,\"userCount\":" + userCount + ",\"postCount\":" + postCount + "}");
        } else if ("/logout".equals(path)) {
            req.getSession().invalidate();
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
        }
    }
}
