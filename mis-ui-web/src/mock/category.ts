import Mock from 'mockjs'
/** 静态指定mock数据 */
function createStaticCategoryList() {
  return [
    { categoryId: 1, parentId: 0, categoryName: '文具' },
    { categoryId: 2, parentId: 0, categoryName: '日用品' },
    { categoryId: 3, parentId: 0, categoryName: '手机' },
    { categoryId: 4, parentId: 0, categoryName: '电脑' },
  ]
}

/** Mock模拟生成 */
function createMockCategoryList() {
  const mockData = Mock.mock({
    'list|10': [
      {
        'categoryId|+1': 1,
        parentId: 0,
        categoryName: '@ctitle',
      },
    ],
  })
  return mockData.list
}

/** 配置Mock接口 */

export default [
  {
    url: '/api/category/listAll',
    method: 'get',
    response: () => {
      // 以下二选一，根据项目实际需要选择
      // const categoryList = createStaticCategoryList() // 静态指定
      const categoryList = createMockCategoryList() // mock生成
      return { code: 200, msg: '查询成功', data: categoryList }
    },
  },
]
