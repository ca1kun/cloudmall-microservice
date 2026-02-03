import request from "@/utils/request";
export function hello() {
  return request({
    url: "/",
    method: "get",
  });
}