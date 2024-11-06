<script setup>
import MapRoad from './MapRoad.vue';
import wnetwork from '@/wnetwork';

var isShowMap=defineModel('isShowMap')
var mapData=[]
isShowMap.value=false
wnetwork.get("/api/map?id="+sessionStorage.getItem("residenceId"))
    .then(response=>{
        isShowMap.value=response.data.isShowMap
        mapData=response.data.mapData
    })
</script>

<template>
    <div class="map_container">
        <div class="map_view" v-if="isShowMap">
            <MapRoad v-for="road in mapData" :roadData="road"/>
        </div>
        <div class="map_nodata" v-else>

        </div>
    </div>
</template>

<script>
export default {
    methods:{
        changeResidence(){

        }
    }
}
</script>