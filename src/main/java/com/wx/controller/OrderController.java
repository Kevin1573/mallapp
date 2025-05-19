package com.wx.controller;

import com.alibaba.fastjson.JSON;
import com.wx.common.model.Response;
import com.wx.common.model.request.*;
import com.wx.common.model.response.*;
import com.wx.orm.entity.UserAddrDO;
import com.wx.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/orderGoods")
    public Response<OrderGoodsResponse> orderGoods(@RequestBody OrderGoodsRequest request) {
        try {
            return Response.success(orderService.orderGoods(request));
        } catch (Exception e) {
            log.error("OrderGoods exception, request = {}", JSON.toJSONString(request), e);
            return Response.failure("下单出错了...");
        }
    }

    @RequestMapping(value = "/queryGoods", method = {RequestMethod.POST})
    public Response<QueryGoodsResponse> queryGoods(@RequestBody PageQueryGoodsRequest request) {
        try {
            return Response.success(orderService.queryGoods(request));
        } catch (Exception e) {
            log.error("queryGoods exception, request = {}", JSON.toJSONString(request), e);
            return Response.failure("queryGoods exception");
        }
    }

//    @RequestMapping(value = "/wxPayCallback", method = {RequestMethod.POST})
//    public Response wxPayCallback(@RequestBody JSONObject request) {
//        try {
//            orderService.wxPayCallback(request);
//            return Response.success();
//        } catch (Exception e) {
//            log.error("wxPayCallback exception, request = {}", JSON.toJSONString(request), e);
//            return Response.failure("wxPayCallback exception");
//        }
//    }


    @PostMapping("/getGoodsDetailById")
    public Response<QueryGoodsByIdResponse> getGoodsDetailById(@RequestBody QueryGoodsByIdRequest request) {
        try {
            return Response.success(orderService.queryGoodsById(request));
        } catch (Exception e) {
            log.error("queryOrderStatus exception, request = {}", JSON.toJSONString(request), e);
            return Response.failure("getGoodsDetailById exception");
        }
    }

    @RequestMapping(value = "/queryOrderStatus", method = {RequestMethod.POST})
    public Response<Boolean> queryOrderStatus(@RequestBody QueryOrderStatusRequest request) {
        try {
            return Response.success(orderService.queryOrderStatus(request));
        } catch (Exception e) {
            log.error("queryOrderStatus exception, request = {}", JSON.toJSONString(request), e);
            return Response.failure("queryOrderStatus exception");
        }
    }

    @RequestMapping(value = "/closeOrder", method = {RequestMethod.POST})
    public Response closeOrder(@RequestBody CloseOrderRequest request) {
        try {
            orderService.closeOrder(request);
            return Response.success();
        } catch (Exception e) {
            log.error("closeOrder exception, request = {}", JSON.toJSONString(request), e);
            return Response.failure("closeOrder exception");
        }
    }

    @RequestMapping(value = "/addShoppingCar", method = {RequestMethod.POST})
    public Response addShoppingCar(@RequestBody AddShoppingCarRequest request) {
        try {
            orderService.addShoppingCar(request);
            return Response.success();
        } catch (Exception e) {
            log.error("addShoppingCar exception, request = {}", JSON.toJSONString(request), e);
            return Response.failure("addShoppingCar exception");
        }
    }

    @RequestMapping(value = "/editShoppingCarNum", method = {RequestMethod.POST})
    public Response editShoppingCarNum(@RequestBody AddShoppingCarRequest request) {
        try {
            orderService.editShoppingCarNum(request);
            return Response.success();
        } catch (Exception e) {
            log.error("editShoppingCarNum exception, request = {}", JSON.toJSONString(request), e);
            return Response.failure("editShoppingCarNum exception");
        }
    }

    @RequestMapping(value = "/queryGoodsById", method = {RequestMethod.POST})
    public Response queryGoodsById(@RequestBody QueryGoodsByIdRequest request) {
        try {
            return Response.success(orderService.queryGoodsById(request));
        } catch (Exception e) {
            log.error("queryGoodsById exception, request = {}", JSON.toJSONString(request), e);
            return Response.failure("queryGoodsById exception");
        }
    }

    @RequestMapping(value = "/getGoodsByName", method = {RequestMethod.POST})
    public Response<List<QueryGoodsModel>> getGoodsByName(@RequestBody GetGoodsByNameRequest request) {
        try {
            return Response.success(orderService.getGoodsByName(request));
        } catch (Exception e) {
            log.error("getGoodsByName exception, request = {}", JSON.toJSONString(request), e);
            return Response.failure("getGoodsByName exception");
        }
    }

    @RequestMapping(value = "/queryCarOrder", method = {RequestMethod.POST})
    public Response<List<QueryCarOrdersResponse>> queryCarOrder(@RequestBody AddShoppingCarRequest request) {
        try {
            return Response.success(orderService.queryCarOrder(request));
        } catch (Exception e) {
            log.error("queryCarOrder exception, request = {}", JSON.toJSONString(request), e);
            return Response.failure("queryCarOrder exception");
        }
    }

    @RequestMapping(value = "/deleteCarOrderById", method = {RequestMethod.POST})
    public Response deleteCarOrderById(@RequestBody AddShoppingCarRequest request) {
        try {
            orderService.deleteCarOrderById(request);
            return Response.success();
        } catch (Exception e) {
            log.error("deleteCarOrderById exception, request = {}", JSON.toJSONString(request), e);
            return Response.failure("deleteCarOrderById exception");
        }
    }

    @RequestMapping(value = "/getOrderDetailById", method = {RequestMethod.POST})
    public Response<QueryOrderHistoryModel> getOrderDetailById(@RequestBody GetOrderDetailByTradeNo request) {
        try {
            return Response.success(orderService.getOrderDetailById(request));
        } catch (Exception e) {
            log.error("getOrderDetailById exception, request = {}", JSON.toJSONString(request), e);
            return Response.failure("getOrderDetailById exception " + e.getMessage());
        }
    }

    @RequestMapping(value = "/commitOrder", method = {RequestMethod.POST})
    public Response<CommitOrderResponse> commitOrder(@RequestBody OrderGoodsRequest request) {
        try {
            return Response.success(orderService.commitOrder(request));
        } catch (Exception e) {
            log.error("commitOrder exception, request = {}", JSON.toJSONString(request), e);
            return Response.failure("commitOrder exception");
        }
    }

    @RequestMapping(value = "/addOrUpdateUserAddr", method = {RequestMethod.POST})
    public Response addOrUpdateUserAddr(@RequestBody UserAddrRequest request) {
        try {
            orderService.addOrUpdateUserAddr(request);
            return Response.success();
        } catch (Exception e) {
            log.error("addOrUpdateUserAddr exception, request = {}", JSON.toJSONString(request), e);
            return Response.failure("addOrUpdateUserAddr exception");
        }
    }

    @RequestMapping(value = "/deleteUserAddr", method = {RequestMethod.POST})
    public Response deleteUserAddr(@RequestBody UserAddrRequest request) {
        try {
            orderService.deleteUserAddr(request);
            return Response.success();
        } catch (Exception e) {
            log.error("deleteUserAddr exception, request = {}", JSON.toJSONString(request), e);
            return Response.failure("deleteUserAddr exception");
        }
    }

    @RequestMapping(value = "/selectUserAddrList", method = {RequestMethod.POST})
    public Response<List<UserAddrDO>> selectUserAddrList(@RequestBody UserAddrRequest request) {
        try {
            return Response.success(orderService.selectUserAddrList(request));
        } catch (Exception e) {
            log.error("selectUserAddrList exception, request = {}", JSON.toJSONString(request), e);
            return Response.failure("selectUserAddrList exception");
        }
    }

    @RequestMapping(value = "/setAddrDefaul", method = {RequestMethod.POST})
    public Response setAddrDefaul(@RequestBody UserAddrRequest request) {
        try {
            orderService.setAddrDefaul(request);
            return Response.success();
        } catch (Exception e) {
            log.error("setAddrDefaul exception, request = {}", JSON.toJSONString(request), e);
            return Response.failure("setAddrDefaul exception");
        }
    }

    @RequestMapping(value = "/match", method = {RequestMethod.POST})
    public Response<MatchAddrResponse> match(@RequestBody UserAddrRequest request) {
        try {
            return Response.success(orderService.match(request));
        } catch (Exception e) {
            log.error("match exception, request = {}", JSON.toJSONString(request), e);
            return Response.failure("match exception");
        }
    }

    @RequestMapping(value = "/returnOrder", method = {RequestMethod.POST})
    public Response<String> returnOrder(@RequestBody OrderRequest request) {
        try {
            return Response.success(orderService.returnOrder(request));
        } catch (Exception e) {
            log.error("returnOrder exception, request = {}", JSON.toJSONString(request), e);
            return Response.failure("returnOrder exception");
        }
    }

    @RequestMapping(value = "/packorder", method = {RequestMethod.POST})
    public Response packorder(@RequestBody OrderRequest request) {
        try {
            orderService.packorder(request);
            return Response.success();
        } catch (Exception e) {
            log.error("packorder exception, request = {}", JSON.toJSONString(request), e);
            return Response.failure("packorder exception");
        }
    }
}
