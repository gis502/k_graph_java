package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.entity.PlotIconmanagement;
import com.ruoyi.system.service.PlotIconmanagementService;
import org.apache.commons.io.FileUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Base64;
import java.util.List;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;


@RestController
@RequestMapping("/system/ploticon")
public class PlotIconmanagementController {

    @Resource
    private PlotIconmanagementService plotIconmanagementService;

    @PostMapping("/getploticon")
    public AjaxResult getploticon() {
        return AjaxResult.success(plotIconmanagementService.list());
    }

    @PostMapping("/deleteploticon/{uuid}")
    @Log(title = "标会图片管理", businessType = BusinessType.DELETE)
    public AjaxResult deletePlotIcon(@PathVariable("uuid") String id) {
        // 从数据库获取 plotIcon 对象
        PlotIconmanagement plotIcon = plotIconmanagementService.getById(id);
        if (plotIcon != null) {
            // 确定文件路径
            String projectPath = System.getProperty("user.dir");
            String filePath = projectPath + "/logistics/uploads/PlotsPic/" + plotIcon.getName() + ".png";

            // 删除文件
            File file = new File(filePath);
            if (file.exists() && file.delete()) {
                // 文件删除成功后再删除数据库记录
                plotIconmanagementService.removeById(id);
                return AjaxResult.success("File and record deleted successfully.");
            } else {
                return AjaxResult.error("Failed to delete file.");
            }
        }
        return AjaxResult.error("Record not found.");
    }

    @PostMapping("/updataploticon")
    @Log(title = "标会图片管理", businessType = BusinessType.UPDATE)
    public AjaxResult updataPlotIcon(@RequestBody PlotIconmanagement plotIcon) {
        // 从数据库获取当前记录
        PlotIconmanagement existingPlotIcon = plotIconmanagementService.getById((Serializable) plotIcon.getUuid());
        if (existingPlotIcon == null) {
            return AjaxResult.error("Record not found.");
        }

        // 保存时图片没有变化时，不进行修改
        boolean isBase64 = plotIcon.getImg().startsWith("data:image/jpeg;base64");
        if (!isBase64) {
            plotIcon.setImg(null);
            plotIconmanagementService.updateById(plotIcon);
            return AjaxResult.success("Record updated successfully.");
        }

        // 删除旧图片
        String projectPath = System.getProperty("user.dir");
        String oldFilePath = projectPath + "/logistics/uploads/PlotsPic/" + existingPlotIcon.getName();
        File oldFile = new File(oldFilePath);
        if (oldFile.exists()) {
            oldFile.delete();
        }

        // 保存新图片
        try {
            String base64Data = plotIcon.getImg();
            if (base64Data.contains(",")) {
                base64Data = base64Data.split(",")[1];
            }
            byte[] imageBytes = Base64.getMimeDecoder().decode(base64Data);

            String newFilePath = projectPath + "/logistics/uploads/PlotsPic/" + plotIcon.getName() + ".png";
            File newFile = new File(newFilePath);
            newFile.getParentFile().mkdirs();
            FileUtils.writeByteArrayToFile(newFile, imageBytes);

            plotIcon.setImg(plotIcon.getName());

        } catch (IOException e) {
            e.printStackTrace();
            return AjaxResult.error("Failed to save new image: " + e.getMessage());
        }
        // 更新数据库中的记录
        plotIconmanagementService.updateById(plotIcon);
        return AjaxResult.success("Record updated successfully.");
    }

    @PostMapping("/searchploticon")
    public List<PlotIconmanagement> searchPloticon(@RequestParam("menuName") String menuName) {
        QueryWrapper<PlotIconmanagement> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name", menuName).or().like("describe", menuName).or().like("type", menuName);
        List<PlotIconmanagement> list = plotIconmanagementService.list(queryWrapper);
        return list;
    }

    @PostMapping("/addploticon")
    @Log(title = "标会图片管理", businessType = BusinessType.INSERT)
    public AjaxResult addPlotIcon(@RequestBody PlotIconmanagement plotIcon) throws IOException {
        String base64Data = plotIcon.getImg();
        String imageName = plotIcon.getName(); // 获取作为文件名的 name 字段

        try {
            // 检查 Base64 字符串格式并去掉前缀
            if (base64Data.contains(",")) {
                base64Data = base64Data.split(",")[1];
            }

            // 使用 Base64 MIME 解码器来解码数据
            byte[] imageBytes = Base64.getMimeDecoder().decode(base64Data);

            // 确定文件保存路径
            String projectPath = System.getProperty("user.dir");
            String outputPath = projectPath + "/logistics/uploads/PlotsPic/" + imageName + ".png";
            File outputFile = new File(outputPath);

            // 确保目录存在
            outputFile.getParentFile().mkdirs();

            // 将字节数组写入文件，生成 PNG 图片
            FileUtils.writeByteArrayToFile(outputFile, imageBytes);

            // 更新 plotIcon 的 img 字段为文件名
            plotIcon.setImg(imageName);

            // 保存 plotIcon 对象
            plotIconmanagementService.save(plotIcon);

            return AjaxResult.success("File saved successfully with name: " + imageName);
        } catch (IOException e) {
            e.printStackTrace();
            return AjaxResult.error("Failed to save image: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return AjaxResult.error("Base64 decoding failed: " + e.getMessage());
        }
    }

}
