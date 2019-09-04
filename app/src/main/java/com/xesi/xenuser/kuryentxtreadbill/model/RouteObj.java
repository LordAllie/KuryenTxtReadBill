package com.xesi.xenuser.kuryentxtreadbill.model;

/**
 * Created by Daryll Sabate on 7/21/2017.
 */
public class RouteObj {

    private int idRoute;
    private String routeCode;

    public RouteObj() {
    }

    public RouteObj(int idRoute, String routeCode) {
        this.idRoute = idRoute;
        this.routeCode = routeCode;
    }

    public int getIdRoute() {
        return idRoute;
    }

    public void setIdRoute(int idRoute) {
        this.idRoute = idRoute;
    }

    public String getRouteCode() {
        return routeCode;
    }

    public void setRouteCode(String routeCode) {
        this.routeCode = routeCode;
    }
}
