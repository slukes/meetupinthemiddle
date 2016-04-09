describe("homepage test", function(){
   beforeEach(function(){
       $.get('/index.html', function(page){
           $('document').html(page)
       });
   });

    it("submit button is disabled on page load", function(){
        expect(document.getElementById('hellokitty').getAttribute('disabled')).toBe(false);
    });
});