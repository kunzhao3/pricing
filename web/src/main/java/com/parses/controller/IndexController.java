package com.parses.controller;

import com.parses.server.*;
import com.parses.server.bean.*;
import com.parses.server.constant.ElementCode;
import com.parses.server.mapping.FeeFormulaParamMapping;
import com.parses.server.mapping.FormulaParamMapping;
import com.parses.util.ExlRead;
import com.parses.util.bean.ExlFeeParamBean;
import com.parses.util.constant.SheetIndex;
import ma.glasnost.orika.MapperFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
public class IndexController {
    @Resource
    MapperFacade mapperFacade;
    @Value("${file.upload-dir}")
    private String uploadDir;
    @Autowired
    private CapitalServer capitalServer;
    @Autowired
    private ElementDataServer elementDataServer;
    @Autowired
    private ProductPricingServer productPricingServer;
    @Autowired
    private PricingFeeServer pricingFeeServer;
    @Autowired
    private CreatePricingServer createPricingServer;

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);


    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "请选择要上传的文件");
            return "redirect:/";
        }
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(Objects.requireNonNull(file.getOriginalFilename()));
            if (Files.exists(filePath)) {
                redirectAttributes.addFlashAttribute("message", "文件已存在: " + file.getOriginalFilename());
                return "redirect:/";
            }
            Files.copy(file.getInputStream(), filePath);

            ExlRead exlRead = new ExlRead(uploadDir + file.getOriginalFilename());
            // 资方和资方要素
            List<ElementDataBean> capitalElementList = exlRead.readExcel("ElementDataBean", SheetIndex.CAPITAL_ELEMENT, false);
            String capitalCode = "";
            for (ElementDataBean elementDataBean : capitalElementList) {
                if (elementDataBean.getElementCode().equals(ElementCode.CAPITAL_CODE)) {
                    capitalCode = elementDataBean.getElementData();
                    break;
                }
            }
            CapitalBean capitalBean = capitalServer.createCapital(capitalCode);
            elementDataServer.addCapitalInfo(capitalElementList, capitalBean);
            // 产品定价
            List<ProductPricingBean> productPricingBeans = exlRead.readExcel("ProductPricingBean", SheetIndex.PRODUCT_PRICING, false);
            productPricingServer.addProductPricingInfo(productPricingBeans, capitalBean);
            // 产品费项
            List<ExlFeeParamBean> lists = exlRead.readExcel("ExlFeeParamBean", SheetIndex.PRODUCT_FEE, false);
            List<PricingFeeBean> pricingFeeBeans = new ArrayList<>();
            Class<?> exlFeeParamBeanClass = ExlFeeParamBean.class;
            Field[] exlFeeParamBeanFields = exlFeeParamBeanClass.getDeclaredFields();
            for (ExlFeeParamBean exlFeeParamBean : lists) {
                PricingFeeBean pricingFeeBean = this.mapperFacade.map(exlFeeParamBean, PricingFeeBean.class);
                List<FormulaParamMapping> feeFormulaParams = Objects.requireNonNull(FeeFormulaParamMapping.getFeeParamsMapping(pricingFeeBean.getFeeCode())).getParams();
                List<FormulaParamModel> formulaParamList = new ArrayList<>();
                for (FormulaParamMapping feeFormulaParam : feeFormulaParams) {
                    for (Field exlFeeParamBeanField : exlFeeParamBeanFields) {
                        // 利用反射获取字段
                        exlFeeParamBeanField.setAccessible(true);
                        if (exlFeeParamBeanField.getName().equals(feeFormulaParam.getParamCode())
                                && Objects.nonNull(exlFeeParamBeanField.get(exlFeeParamBean))) {
                            FormulaParamModel formulaParamModel = new FormulaParamModel();
                            formulaParamModel.setParamCode(feeFormulaParam.getParamCode());
                            formulaParamModel.setValue(String.valueOf(exlFeeParamBeanField.get(exlFeeParamBean)));
                            formulaParamList.add(formulaParamModel);
                            break;
                        }
                    }
                }
                pricingFeeBean.setFormulaParamList(formulaParamList);
                pricingFeeBeans.add(pricingFeeBean);
            }
            List<PricingFeeBean> pricingFeeBeanList = pricingFeeServer.addProductFeeInfo(productPricingBeans, pricingFeeBeans, capitalElementList);
            createPricingServer.createPricingProcess(capitalBean, capitalElementList, productPricingBeans, pricingFeeBeanList);
            redirectAttributes.addFlashAttribute("message", "文件上传成功: " + file.getOriginalFilename());
        } catch (Exception e) {
            Path path = Paths.get(uploadDir + file.getOriginalFilename());
            try {
                Files.deleteIfExists(path);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            logger.error("文件上传失败: {} {}", file.getOriginalFilename(), e.getMessage());
            redirectAttributes.addFlashAttribute("message", "文件上传失败: " + e.getMessage());
        }
        return "redirect:/";
    }

}
