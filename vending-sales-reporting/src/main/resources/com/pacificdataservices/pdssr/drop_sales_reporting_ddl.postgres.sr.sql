drop sequence org_id_seq
;--
drop table org cascade
;--
drop table org_datafeed cascade
;--
drop table org_mfr cascade
;--
drop table org_distrib cascade
;--
drop table org_customer cascade
;--
drop sequence distributor_customer_id_seq
;--
drop table distributor_customer cascade
;--
drop sequence product_id_seq;
;--
drop table product cascade
;--
drop sequence etl_file_id_seq
;--
drop table etl_file cascade
;--
drop sequence etl_customer_id_seq
;--
drop table etl_customer cascade
;--
drop sequence customer_info_id_seq
;--
drop table customer_info cascade
;--
drop table etl_customer_tot cascade
;--
drop table etl_inventory cascade
;--
drop table etl_inventory_tot cascade
;--
drop table etl_sale cascade
;--
drop table etl_sale_tot cascade
;--
drop table product_pkg cascade
;--
drop table product_nomen cascade
;--
drop table customer cascade
;--
drop table product_suspect_hdr cascade
;--
drop table product_suspect_dtl cascade
;--
drop table post_sale cascade
;--
drop table validated_address cascade
;--
drop view load_conditions
;--
drop view etl_sale_post_stats
;--
drop view sale_product_cust_vw
;--
drop view etl_customer_stat
;--
drop view etl_sale_stat 
;--
drop view etl_post_sale_stat
;--
drop view etl_file_stat 
;--
drop view etl_sale_unknown_item 
;--
drop view product_vw 
;--
drop view product_nomen_xref 