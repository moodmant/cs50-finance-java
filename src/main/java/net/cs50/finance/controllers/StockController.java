package net.cs50.finance.controllers;

import net.cs50.finance.models.Stock;
import net.cs50.finance.models.StockHolding;
import net.cs50.finance.models.StockLookupException;
import net.cs50.finance.models.dao.StockHoldingDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Chris Bay on 5/17/15.
 */
@Controller
public class StockController extends AbstractFinanceController {

    @Autowired
    StockHoldingDao stockHoldingDao;

    @RequestMapping(value = "/quote", method = RequestMethod.GET)
    public String quoteForm(Model model) {

        // pass data to template
        model.addAttribute("title", "Quote");
        model.addAttribute("quoteNavClass", "active");
        return "quote_form";
    }

    @RequestMapping(value = "/quote", method = RequestMethod.POST)
    public String quote(String symbol, Model model) throws StockLookupException {

        //- Implement quote lookup
        Stock myStock;
        try {
            myStock = Stock.lookupStock(symbol);
        } catch (StockLookupException e) {
            e.printStackTrace();
            return displayError("Unable to lookup stock", model);
        }

        // pass data to template
        model.addAttribute("title", "Quote");
        model.addAttribute("quoteNavClass", "active");
        model.addAttribute("stock_desc", myStock.getName());
        model.addAttribute("stock_price", myStock.getPrice());

        return "quote_display";
    }

    @RequestMapping(value = "/buy", method = RequestMethod.GET)
    public String buyForm(Model model) {

        model.addAttribute("title", "Buy");
        model.addAttribute("action", "/buy");
        model.addAttribute("buyNavClass", "active");
        return "transaction_form";
    }

    @RequestMapping(value = "/buy", method = RequestMethod.POST)
    public String buy(String symbol, int numberOfShares, HttpServletRequest request, Model model) {

        //- Implement buy action
        StockHolding holding = null;
        try {
            holding = StockHolding.buyShares(getUserFromSession(request), symbol, numberOfShares);
        } catch (StockLookupException a){
            a.printStackTrace();
            return displayError("Need more money for that!", model);
        }

        stockHoldingDao.save(holding);

        model.addAttribute("title", "Buy");
        model.addAttribute("action", "/buy");
        model.addAttribute("buyNavClass", "active");

        return "transaction_confirm";
    }

    @RequestMapping(value = "/sell", method = RequestMethod.GET)
    public String sellForm(Model model) {
        model.addAttribute("title", "Sell");
        model.addAttribute("action", "/sell");
        model.addAttribute("sellNavClass", "active");
        return "transaction_form";
    }

    @RequestMapping(value = "/sell", method = RequestMethod.POST)
    public String sell(String symbol, int numberOfShares, HttpServletRequest request, Model model) {

        //- Implement sell action
        StockHolding holding = null;
        try {
            holding = StockHolding.sellShares(getUserFromSession(request), symbol, numberOfShares);
        } catch (StockLookupException b){
            b.printStackTrace();
        }

        stockHoldingDao.save(holding);


        model.addAttribute("title", "Sell");
        model.addAttribute("action", "/sell");
        model.addAttribute("sellNavClass", "active");

        return "transaction_confirm";
    }

}
