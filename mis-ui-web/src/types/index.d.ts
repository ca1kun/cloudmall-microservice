import request from '@/utils/request'
export interface SaleQueryParams {
  pageNum: number
  pageSize: number
  saleId?: number
  saleNo?: string
  status?: string
}

export interface PaymentDTO {
  paymentId: number
  paymentNo: string
  payMethod: string
}

export interface SaleDTO {
  saleId: number
  saleNo: string
  total: number
  status: string
  payment: PaymentDTO
}


export interface PaymentForm {
  saleId: number
  payMethod: string
  amount: number
}