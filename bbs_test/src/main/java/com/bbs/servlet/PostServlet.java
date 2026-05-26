package com.bbs.servlet;

import com.bbs.entity.Post;
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
import java.util.stream.Collectors;

@WebServlet("/post/*")
public class PostServlet extends HttpServlet {
    private final PostService postService = new PostService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String path = req.getPathInfo();

        if ("/list".equals(path)) {
            String sectionId = req.getParameter("sectionId");
            String page = req.getParameter("page");
            String size = req.getParameter("size");
            List<Post> list;
            if (sectionId != null && !sectionId.isEmpty()) {
                list = postService.getBySection(Long.parseLong(sectionId), page != null ? Integer.parseInt(page) : 1, size != null ? Integer.parseInt(size) : 20);
            } else {
                String keyword = req.getParameter("keyword");
                list = postService.search(keyword != null ? keyword : "", page != null ? Integer.parseInt(page) : 1, size != null ? Integer.parseInt(size) : 20);
            }
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                Post p = list.get(i);
                sb.append("{\"id\":").append(p.getId())
                  .append(",\"title\":\"").append(p.getTitle() != null ? p.getTitle() : "").append("\"")
                  .append(",\"sectionId\":").append(p.getSectionId())
                  .append(",\"sectionName\":\"").append(p.getSection() != null && p.getSection().getName() != null ? p.getSection().getName() : "").append("\"")
                  .append(",\"userId\":").append(p.getUserId())
                  .append(",\"authorName\":\"").append(p.getAuthor() != null && p.getAuthor().getNickname() != null ? p.getAuthor().getNickname() : "").append("\"")
                  .append(",\"pointsReward\":").append(p.getPointsReward())
                  .append(",\"frozenPoints\":").append(p.getFrozenPoints() != null ? p.getFrozenPoints() : 0)
                  .append(",\"isDemand\":").append(p.getIsDemand())
                  .append(",\"isSolved\":").append(p.getIsSolved() != null ? p.getIsSolved() : 0)
                  .append(",\"isTop\":").append(p.getIsTop())
                  .append(",\"isGood\":").append(p.getIsGood())
                  .append(",\"viewCount\":").append(p.getViewCount())
                  .append(",\"replyCount\":").append(p.getReplyCount())
                  .append(",\"createTime\":\"").append(p.getCreateTime() != null ? p.getCreateTime().toString() : "").append("\"")
                  .append("}");
                if (i < list.size() - 1) sb.append(",");
            }
            sb.append("]");
            resp.getWriter().write("{\"success\":true,\"data\":" + sb + "}");
        } else if ("/myPosts".equals(path)) {
            HttpSession session = req.getSession();
            User user = (User) session.getAttribute("user");
            if (user == null) {
                resp.getWriter().write("{\"success\":false,\"message\":\"请先登录\"}");
                return;
            }
            List<Post> list = postService.getMyPosts(user.getId());
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                Post p = list.get(i);
                sb.append("{\"id\":").append(p.getId())
                  .append(",\"title\":\"").append(p.getTitle() != null ? p.getTitle() : "").append("\"")
                  .append(",\"sectionId\":").append(p.getSectionId())
                  .append(",\"sectionName\":\"").append(p.getSection() != null && p.getSection().getName() != null ? p.getSection().getName() : "").append("\"")
                  .append(",\"userId\":").append(p.getUserId())
                  .append(",\"authorName\":\"").append(p.getAuthor() != null && p.getAuthor().getNickname() != null ? p.getAuthor().getNickname() : "").append("\"")
                  .append(",\"replyCount\":").append(p.getReplyCount())
                  .append(",\"createTime\":\"").append(p.getCreateTime() != null ? p.getCreateTime().toString() : "").append("\"")
                  .append("}");
                if (i < list.size() - 1) sb.append(",");
            }
            sb.append("]");
            resp.getWriter().write("{\"success\":true,\"data\":" + sb + "}");
        } else if ("/allPosts".equals(path)) {
            List<Post> list = postService.getAllPosts();
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                Post p = list.get(i);
                sb.append("{\"id\":").append(p.getId())
                  .append(",\"title\":\"").append(p.getTitle() != null ? p.getTitle() : "").append("\"")
                  .append(",\"sectionId\":").append(p.getSectionId())
                  .append(",\"sectionName\":\"").append(p.getSection() != null && p.getSection().getName() != null ? p.getSection().getName() : "").append("\"")
                  .append(",\"userId\":").append(p.getUserId())
                  .append(",\"authorName\":\"").append(p.getAuthor() != null && p.getAuthor().getNickname() != null ? p.getAuthor().getNickname() : "").append("\"")
                  .append(",\"isTop\":").append(p.getIsTop())
                  .append(",\"isGood\":").append(p.getIsGood())
                  .append(",\"isDemand\":").append(p.getIsDemand())
                  .append(",\"pointsReward\":").append(p.getPointsReward() != null ? p.getPointsReward() : 0)
                  .append(",\"replyCount\":").append(p.getReplyCount())
                  .append(",\"createTime\":\"").append(p.getCreateTime() != null ? p.getCreateTime().toString() : "").append("\"")
                  .append("}");
                if (i < list.size() - 1) sb.append(",");
            }
            sb.append("]");
            resp.getWriter().write("{\"success\":true,\"data\":" + sb + "}");
        } else if (path != null && path.matches("/\\d+")) {
            Long id = Long.parseLong(path.substring(1));
            Post post = postService.getById(id);
            if (post != null) {
                String json = "{\"success\":true,\"post\":{\"id\":" + post.getId() + ",\"title\":\"" + post.getTitle() + "\",\"content\":\"" + (post.getContent() != null ? post.getContent().replace("\"", "\\\"") : "") + "\",\"sectionId\":" + post.getSectionId() + ",\"userId\":" + post.getUserId() + ",\"authorName\":\"" + (post.getAuthor() != null ? post.getAuthor().getNickname() : "") + "\",\"pointsReward\":" + post.getPointsReward() + ",\"frozenPoints\":" + (post.getFrozenPoints() != null ? post.getFrozenPoints() : 0) + ",\"isDemand\":" + post.getIsDemand() + ",\"isSolved\":" + (post.getIsSolved() != null ? post.getIsSolved() : 0) + ",\"isTop\":" + post.getIsTop() + ",\"isGood\":" + post.getIsGood() + ",\"viewCount\":" + post.getViewCount() + ",\"replyCount\":" + post.getReplyCount() + ",\"createTime\":\"" + (post.getCreateTime() != null ? post.getCreateTime().toString() : "") + "\"}}";
                resp.getWriter().write(json);
            } else {
                resp.getWriter().write("{\"success\":false,\"message\":\"帖子不存在\"}");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            resp.getWriter().write("{\"success\":false,\"message\":\"请先登录\"}");
            return;
        }

        String path = req.getPathInfo();
        if ("/publish".equals(path)) {
            Post post = new Post();
            post.setSectionId(Long.parseLong(req.getParameter("sectionId")));
            post.setUserId(user.getId());
            post.setTitle(req.getParameter("title"));
            post.setContent(req.getParameter("content"));
            post.setPointsReward(Integer.parseInt(req.getParameter("pointsReward") != null ? req.getParameter("pointsReward") : "0"));
            post.setIsDemand(Integer.parseInt(req.getParameter("isDemand") != null ? req.getParameter("isDemand") : "0"));
            Post result = postService.publish(post);
            if (result != null) {
                resp.getWriter().write("{\"success\":true,\"message\":\"发布成功\",\"postId\":" + result.getId() + "}");
            } else {
                resp.getWriter().write("{\"success\":false,\"message\":\"积分不足或发布失败\"}");
            }
        } else if ("/reply".equals(path)) {
            Reply reply = new Reply();
            reply.setPostId(Long.parseLong(req.getParameter("postId")));
            reply.setUserId(user.getId());
            reply.setContent(req.getParameter("content"));
            Reply result = postService.reply(reply);
            if (result != null) {
                resp.getWriter().write("{\"success\":true,\"message\":\"回复成功\"}");
            } else {
                resp.getWriter().write("{\"success\":false,\"message\":\"回复失败\"}");
            }
        } else if ("/update".equals(path)) {
            Long postId = Long.parseLong(req.getParameter("id"));
            Post post = postService.getById(postId);
            if (post == null) {
                resp.getWriter().write("{\"success\":false,\"message\":\"帖子不存在\"}");
                return;
            }
            boolean isAdminOrAuthor = user.isAdmin() || user.getId().equals(post.getUserId());
            post.setTitle(req.getParameter("title"));
            post.setContent(req.getParameter("content"));
            if (postService.update(post, isAdminOrAuthor)) {
                resp.getWriter().write("{\"success\":true,\"message\":\"更新成功\"}");
            } else {
                resp.getWriter().write("{\"success\":false,\"message\":\"更新失败\"}");
            }
        } else if ("/delete".equals(path)) {
            Long postId = Long.parseLong(req.getParameter("id"));
            if (postService.delete(postId, user.isAdmin())) {
                resp.getWriter().write("{\"success\":true,\"message\":\"删除成功\"}");
            } else {
                resp.getWriter().write("{\"success\":false,\"message\":\"删除失败\"}");
            }
        } else if ("/deleteMy".equals(path)) {
            Long postId = Long.parseLong(req.getParameter("id"));
            if (postService.deleteMy(postId, user.getId())) {
                resp.getWriter().write("{\"success\":true,\"message\":\"删除成功\"}");
            } else {
                resp.getWriter().write("{\"success\":false,\"message\":\"删除失败\"}");
            }
        } else if ("/setTop".equals(path)) {
            Long postId = Long.parseLong(req.getParameter("id"));
            boolean top = "1".equals(req.getParameter("top"));
            if (postService.setTop(postId, top)) {
                resp.getWriter().write("{\"success\":true,\"message\":\"设置成功\"}");
            } else {
                resp.getWriter().write("{\"success\":false,\"message\":\"设置失败\"}");
            }
        } else if ("/setGood".equals(path)) {
            Long postId = Long.parseLong(req.getParameter("id"));
            boolean good = "1".equals(req.getParameter("good"));
            if (postService.setGood(postId, good)) {
                resp.getWriter().write("{\"success\":true,\"message\":\"设置成功\"}");
            } else {
                resp.getWriter().write("{\"success\":false,\"message\":\"设置失败\"}");
            }
        } else if ("/acceptAnswer".equals(path)) {
            Long replyId = Long.parseLong(req.getParameter("replyId"));
            if (postService.acceptAnswer(replyId, user.getId())) {
                resp.getWriter().write("{\"success\":true,\"message\":\"采纳成功\"}");
            } else {
                resp.getWriter().write("{\"success\":false,\"message\":\"采纳失败\"}");
            }
        } else if ("/cancelDemand".equals(path)) {
            Long postId = Long.parseLong(req.getParameter("id"));
            if (postService.cancelDemand(postId, user.getId())) {
                resp.getWriter().write("{\"success\":true,\"message\":\"取消成功\"}");
            } else {
                resp.getWriter().write("{\"success\":false,\"message\":\"取消失败\"}");
            }
        } else if ("/cleanupOrphanReplies".equals(path)) {
            if (!user.isAdmin()) {
                resp.getWriter().write("{\"success\":false,\"message\":\"无权限\"}");
                return;
            }
            int count = postService.cleanupOrphanReplies();
            resp.getWriter().write("{\"success\":true,\"count\":" + count + "}");
        }
    }
}