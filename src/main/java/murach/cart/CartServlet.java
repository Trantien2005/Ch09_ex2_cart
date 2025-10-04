package murach.cart;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import murach.business.*;
import murach.data.ProductIO;

public class CartServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ServletContext sc = getServletContext();
        String action = request.getParameter("action");
        if (action == null) {
            action = "cart";
        }

        String url = "/index.jsp";
        HttpSession session = request.getSession();
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
        }

        if (action.equals("shop")) {
            url = "/index.jsp";
        } else if (action.equals("cart")) {
        	String mode = request.getParameter("mode");
            String productCode = request.getParameter("productCode");
            String quantityString = request.getParameter("quantity");
            int quantity = 1;  // default
            if (productCode == null || productCode.isEmpty()) {
                response.sendRedirect("index.jsp");
                return;
            }

            try {
                quantity = Integer.parseInt(quantityString);
                if (quantity < 0) quantity = 1;
            } 
            catch (NumberFormatException e) {
                quantity = 1;
            }
            String path = sc.getRealPath("/WEB-INF/products.txt");
            Product product = ProductIO.getProduct(productCode, path);

            LineItem item = new LineItem();
            item.setProduct(product);
            item.setQuantity(quantity);
            if (quantity > 0) {
            	if("update".equals(mode)) {
                	cart.updateItem(item);
                }
            	else {
                cart.addItem(item);
            }
            } else {
                cart.removeItem(item);
            }
            
            session.setAttribute("cart", cart);
            response.sendRedirect(request.getContextPath() + "/cart?action=cartview");
            return;
            
        } else if (action.equals("checkout")) {
            url = "/checkout.jsp";
        }
        else if("cartview".equals(action)) {
        	url = "/cart.jsp";
        }
        if (!response.isCommitted()) {
            sc.getRequestDispatcher(url).forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}
