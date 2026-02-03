package edu.scau.mis.api.controller;

import edu.scau.mis.common.domain.ApiResult;
import edu.scau.mis.common.domain.Category;
import edu.scau.mis.product.service.ICategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/category")
@Tag(name = "分类管理")
public class CategoryController {

    private final ICategoryService categoryService;

    @Autowired
    public CategoryController(ICategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "根据ID查询分类")
    @GetMapping("/{categoryId}")
    @ApiResponse(responseCode = "200", description = "查询分类成功", content = @Content(schema = @Schema(implementation = Category.class)))
    public ApiResult<Category> getById(
            @Parameter(description = "分类ID", in = ParameterIn.PATH, required = true)
            @PathVariable("categoryId") Long categoryId) {
        Category category = categoryService.getCategoryById(categoryId);
        return category == null ? ApiResult.noContent() : ApiResult.success(category);
    }

    @Operation(summary = "查询所有分类")
    @GetMapping("/listAll")
    public ApiResult<List<Category>> listAll() {
        List<Category> categories = categoryService.getAllCategories();
        return categories.isEmpty() ? ApiResult.noContent() : ApiResult.success(categories);
    }

    @Operation(summary = "根据参数查询分类")
    @GetMapping("/listByParams")
    public ApiResult<List<Category>> listByParams(Category category) {
        // 如果需要根据参数查询，需要在Service层添加对应方法
        List<Category> categories = categoryService.getAllCategories(); // 暂时返回所有
        return categories.isEmpty() ? ApiResult.noContent() : ApiResult.success(categories);
    }

    @Operation(summary = "分页查询分类")
    @GetMapping("/page")
    public ApiResult listByPage(@RequestParam("pageNum") Integer pageNum,
                                @RequestParam("pageSize") Integer pageSize,
                                Category category) {
        List<Category> categoryList = categoryService.getAllCategories(); // 暂时返回所有
        HashMap<String, Object> result = new HashMap();
        result.put("list", categoryList);
        result.put("total", categoryList.size());
        return ApiResult.success(result);
    }

    @Operation(summary = "新增分类")
    @PostMapping("/add")
    public ApiResult<String> add(@RequestBody Category category) {
        int rows = categoryService.insertCategory(category);
        return rows > 0 ? ApiResult.success("添加成功") : ApiResult.fail("添加失败");
    }

    @Operation(summary = "修改分类")
    @PutMapping("/update")
    public ApiResult<String> update(@RequestBody Category category) {
        int rows = categoryService.updateCategory(category);
        return rows > 0 ? ApiResult.success("修改成功") : ApiResult.fail("修改失败");
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/delete/{categoryId}")
    public ApiResult<String> delete(@PathVariable("categoryId") Long categoryId) {
        boolean rows = categoryService.deleteCategoryById(categoryId);
        return rows ? ApiResult.success("删除成功") : ApiResult.fail("删除失败");
    }
}