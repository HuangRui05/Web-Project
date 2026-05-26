package com.bbs.servlet;

import com.bbs.dao.ReplyDao;
import com.bbs.entity.Reply;
import com.bbs.entity.User;
import com.bbs.service.PostService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/reply/*")
public class ReplyServlet extends HttpServlet {
    private final PostService postService = new PostService();
    private final ReplyDao replyDao = new ReplyDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String path = req.getPathInfo();

        if ("/listByUser".equals(path)) {
            HttpSession session = req.getSession();
            User user = (User) session.getAttribute("user");
            if (user == null) {
                resp.getWriter().write("{\"success\":false,\"message\":\"请先登录\"}");
                return;
            }
            List<Reply> list = replyDao.findByUser(user.getId());
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                Reply r = list.get(i);
                sb.append("{\"id\":").append(r.getId())
                  .append(",\"postId\":").append(r.getPostId())
                  .append(",\"userId\":").append(r.getUserId())
                  .append(",\"authorName\":\"").append(r.getAuthor() != null && r.getAuthor().getNickname() != null ? r.getAuthor().getNickname() : "").append("\"")
                  .append(",\"content\":\"").append(r.getContent() != null ? r.getContent().replace("\"", "\\\"") : "").append("\"")
                  .append(",\"isAccept\":").append(r.getIsAccept())
                  .append(",\"pointsEarned\":").append(r.getPointsEarned())
                  .append(",\"createTime\":\"").append(r.getCreateTime() != null ? r.getCreateTime().toString() : "").append("\"")
                  .append("}");
                if (i < list.size() - 1) sb.append(",");
            }
            sb.append("]");
            resp.getWriter().write("{\"success\":true,\"data\":" + sb + "}");
        } else if ("/listAll".equals(path)) {
            List<Reply> list = replyDao.findAll();
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                Reply r = list.get(i);
                sb.append("{\"id\":").append(r.getId())
                  .append(",\"postId\":").append(r.getPostId())
                  .append(",\"userId\":").append(r.getUserId())
                  .append(",\"authorName\":\"").append(r.getAuthor() != null && r.getAuthor().getNickname() != null ? r.getAuthor().getNickname() : "").append("\"")
                  .append(",\"content\":\"").append(r.getContent() != null ? r.getContent().replace("\"", "\\\"") : "").append("\"")
                  .append(",\"isAccept\":").append(r.getIsAccept())
                  .append(",\"pointsEarned\":").append(r.getPointsEarned())
                  .append(",\"createTime\":\"").append(r.getCreateTime() != null ? r.getCreateTime().toString() : "").append("\"")
                  .append("}");
                if (i < list.size() - 1) sb.append(",");
            }
            sb.append("]");
            resp.getWriter().write("{\"success\":true,\"data\":" + sb + "}");
        } else if (path != null && path.matches("/\\d+")) {
            Long postId = Long.parseLong(path.substring(1));
            List<Reply> list = postService.getReplies(postId);
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                Reply r = list.get(i);
                sb.append("{\"id\":").append(r.getId())
                  .append(",\"postId\":").append(r.getPostId())
                  .append(",\"userId\":").append(r.getUserId())
                  .append(",\"authorName\":\"").append(r.getAuthor() != null && r.getAuthor().getNickname() != null ? r.getAuthor().getNickname() : "").append("\"")
                  .append(",\"content\":\"").append(r.getContent() != null ? r.getContent().replace("\"", "\\\"") : "").append("\"")
                  .append(",\"isAccept\":").append(r.getIsAccept())
                  .append(",\"pointsEarned\":").append(r.getPointsEarned())
                  .append(",\"createTime\":\"").append(r.getCreateTime() != null ? r.getCreateTime().toString() : "").append("\"")
                  .append("}");
                if (i < list.size() - 1) sb.append(",");
            }
            sb.append("]");
            resp.getWriter().write("{\"success\":true,\"data\":" + sb + "}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String path = req.getPathInfo();
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");

        if ("/delete".equals(path)) {
            if (user == null) {
                resp.getWriter().write("{\"success\":false,\"message\":\"请先登录\"}");
                return;
            }
            Long replyId = Long.parseLong(req.getParameter("id"));
            Reply reply = replyDao.findById(replyId);
            if (reply == null) {
                resp.getWriter().write("{\"success\":false,\"message\":\"评论不存在\"}");
                return;
            }
            // 只有评论作者或管理员可以删除
            boolean isAdmin = user.isAdmin();
            boolean isAuthor = reply.getUserId().equals(user.getId());
            if (!isAdmin && !isAuthor) {
                resp.getWriter().write("{\"success\":false,\"message\":\"无权删除\"}");
                return;
            }
            if (replyDao.delete(replyId)) {
                resp.getWriter().write("{\"success\":true,\"message\":\"删除成功\"}");
            } else {
                resp.getWriter().write("{\"success\":false,\"message\":\"删除失败\"}");
            }
        }
    }
}