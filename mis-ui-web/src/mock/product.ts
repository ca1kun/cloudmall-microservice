import Mock from 'mockjs'
/** 静态指定产品数据 */
function createStaticProductList() {
  return [
    {
      productId: 8,
      productSn: "1008",
      productName: "Pura 70 Pro",
      productDescription: "超聚光微距长焦，超高速风驰闪拍",
      price: 6499,
      productCategoryId: 3,
      category: {
        categoryId: 3,
        parentId: 0,
        categoryName: "手机"
      },
      imageUrl: "https://res2.vmallres.com/pimages//uomcdn/CN/pms/202404/gbom/6942103119071/800_800_AE94E48F4A6370D6E956B4E722588A5Amp.png",
      detailUrl: "https://www.vmall.com/product/comdetail/index.html?prdId=10086821546239&sbomCode=2601010486504"
    },
    {
      productId: 9,
      productSn: "1009",
      productName: "Mate 60 Pro",
      productDescription: "北斗卫星消息，超可靠玄武架构",
      price: 6999,
      productCategoryId: 3,
      category: {
        categoryId: 3,
        parentId: 0,
        categoryName: "手机"
      },
      imageUrl: "https://res2.vmallres.com/pimages//uomcdn/CN/pms/202309/gbom/6942103117649/800_800_1E1E1E1E1E1E1E1E1E1E1E1E1E1E1E1E1Emp.png",
      detailUrl: "https://www.vmall.com/product/comdetail/index.html?prdId=10086821546240"
    }
  ]
}


/** Mock模拟生成产品数据 */
function createMockProductList() {
  const mockData = Mock.mock({
    'list|15-30': [ // 生成15-30个产品
      {
        'productId|+1': 1,
        'productSn': /100[1-9]\d{0,2}/, // 生成类似1001,1002...的编号
        'productName': '@ctitle(3,8)', // 3-8个中文标题
        'productDescription': '@cword(10,20)', // 10-20个中文描述
        'price|1000-9999': 1, // 价格范围1000-9999
        'productCategoryId': 1, // 分类ID 1-4
        'category': function() {
          const categories = [
            { categoryId: 1, parentId: 0, categoryName: '文具' },
            { categoryId: 2, parentId: 0, categoryName: '日用品' },
            { categoryId: 3, parentId: 0, categoryName: '手机' },
            { categoryId: 4, parentId: 0, categoryName: '电脑' }
          ]
          return categories.find(cat => cat.categoryId === this.productCategoryId)
        },
        'imageUrl': '@image("300x300", "#50B347", "#FFF", "Product")',
        'detailUrl': '@url("https")'
      }
    ]
  })
  return mockData.list
}
/** 配置Mock接口 */

/** 配置Mock接口 */
export default [
  {
    url: '/api/product/listAll',
    method: 'get',
    response: () => {
      // 以下三选一，根据项目实际需要选择
      // const productList = createStaticProductList() // 静态指定
      const productList = createStaticProductList() // mock生成所有产品
      // const productList = createMockProductByCategory(3) // mock生成特定分类产品
      return { code: 200, msg: '查询成功', data: productList }
    },
  },
]
