export interface LoginUser {
  userName: string
  password: string
}

export interface Product {
  productId?: number
  productSn: string
  productName: string
  productDescription?: string
  price: number
  productCategoryId?: number
  category?: Category
  imageUrl?: string
  detailUrl?: string
}

export interface Category {
  categoryId?: number
  parentId: number
  categoryName: string
}

export interface ProductQueryParams {
  pageNum: number
  pageSize: number
  productName?: string
  productSn?: string
  productCategoryId?: number
}

export interface Category {
  categoryId?: number
  parentId: number
  categoryName: string
}

export interface Sale {
  saleId?: number
  saleNo: string
  total: number
  totalQuantity: number
  status: string
}

export interface SaleItem {
  index?: number
  itemSn: string
  productId?: number
  productName: string
  price: number
  quantity: number
}

export interface EnterItemForm {
  itemSn: string
  quantity: number
}

export interface MakePaymentForm {
  saleId?: number
  payMethod: string
  cashTendered: number
  changeDue: number
}
