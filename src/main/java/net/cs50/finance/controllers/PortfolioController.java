package net.cs50.finance.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import net.cs50.finance.models.User;
import net.cs50.finance.models.Stock;
import net.cs50.finance.models.StockLookupException;
import net.cs50.finance.models.StockHolding;


import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by Chris Bay on 5/17/15.
 */
@Controller
public class PortfolioController extends AbstractFinanceController {

    @RequestMapping(value = "/portfolio")
    public String portfolio(HttpServletRequest request, Model model){

        //- Implement portfolio display
        User user = getUserFromSession(request);

        Collection<StockHolding> holdings = user.getPortfolio().values();

        HashMap<String, HashMap<String, String>> printHolding = new HashMap<String, HashMap<String, String>>();

        Stock myStock;
        Iterator it = holdings.iterator();
        while (it.hasNext()){
            StockHolding individual = (StockHolding) it.next();

            HashMap<String, String> newHold = new HashMap<String, String>();

            try {
                myStock = Stock.lookupStock(individual.getSymbol());
            } catch (StockLookupException e) {
                e.printStackTrace();
                return displayError("Unable to lookup stock", model);
            }
            //http://stackoverflow.com/questions/433958/java-decimal-string-format
            String currentValue = String.format("%.2f", myStock.getPrice());
            String totalValue = String.format("%.2f",(myStock.getPrice()*individual.getSharesOwned()));

            newHold.put("name", myStock.getName());
            newHold.put("price", currentValue);
            newHold.put("shares", String.valueOf(individual.getSharesOwned()));
            newHold.put("totalValue", totalValue);
            printHolding.put(myStock.getSymbol(), newHold);
        }


        model.addAttribute("title", "Portfolio");
        model.addAttribute("portfolioNavClass", "active");
        model.addAttribute("holdings", printHolding);

        return "portfolio";
    }

}
